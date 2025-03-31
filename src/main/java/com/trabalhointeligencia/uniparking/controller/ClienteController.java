package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Cliente;
import com.trabalhointeligencia.uniparking.models.Usuario;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import com.trabalhointeligencia.uniparking.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastroCliente";
    }

    @PostMapping
    public String salvarCliente(@Valid @ModelAttribute Cliente cliente, BindingResult result) {
        if(cliente.getNome() != null && cliente.getNome().length() > 100) {
            result.rejectValue("nome", "error.cliente", "O nome deve ter no máximo 100 caracteres");
        }
        if(cliente.getTelefone() != null && cliente.getTelefone().length() > 20) {
            result.rejectValue("telefone", "error.cliente", "O telefone deve ter no máximo 20 caracteres");
        }
        if(cliente.getEmail() != null && cliente.getEmail().length() > 100) {
            result.rejectValue("email", "error.cliente", "O e-mail deve ter no máximo 100 caracteres");
        }
        if(cliente.getEndereco() != null && cliente.getEndereco().length() > 100) {
            result.rejectValue("endereco", "error.cliente", "O endereço deve ter no máximo 100 caracteres");
        }
        if(result.hasErrors()) {
            return "cadastroCliente";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> userOpt = usuarioRepository.findByLogin(username);
        userOpt.ifPresent(cliente::setUsuario);
        clienteService.salvarCliente(cliente);
        return "redirect:/clientes/listar";
    }

    @GetMapping("/listar")
    public String listarClientes(Model model, @RequestParam("page") Optional<Integer> page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Cliente> filtered = clienteService.listarClientesPorUsuario(username);
        int currentPage = page.orElse(1);
        int pageSize = 10;
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        List<Cliente> subList = filtered.subList(start, end);
        Page<Cliente> clientePage = new PageImpl<>(subList, PageRequest.of(currentPage - 1, pageSize), filtered.size());
        model.addAttribute("clientePage", clientePage);
        int totalPages = clientePage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "clients";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable("id") Integer id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if(clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            if (!clientePertenceAoUsuario(cliente, username)) {
                return "redirect:/acesso-negado";
            }

            model.addAttribute("cliente", cliente);
            return "editarCliente";
        }
        return "redirect:/clientes/listar";
    }

    @PostMapping("/editar")
    public String editarCliente(@Valid @ModelAttribute Cliente cliente, BindingResult result) {
        if(cliente.getNome() != null && cliente.getNome().length() > 100) {
            result.rejectValue("nome", "error.cliente", "O nome deve ter no máximo 100 caracteres");
        }
        if(cliente.getTelefone() != null && cliente.getTelefone().length() > 20) {
            result.rejectValue("telefone", "error.cliente", "O telefone deve ter no máximo 20 caracteres");
        }
        if(cliente.getEmail() != null && cliente.getEmail().length() > 100) {
            result.rejectValue("email", "error.cliente", "O e-mail deve ter no máximo 100 caracteres");
        }
        if(cliente.getEndereco() != null && cliente.getEndereco().length() > 200) {
            result.rejectValue("endereco", "error.cliente", "O endereço deve ter no máximo 200 caracteres");
        }
        if(result.hasErrors()) {
            return "editarCliente";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Cliente> clienteExistente = clienteService.buscarClientePorId(cliente.getIdCliente());
        if (clienteExistente.isPresent() && !clientePertenceAoUsuario(clienteExistente.get(), username)) {
            return "redirect:/acesso-negado";
        }

        Optional<Usuario> userOpt = usuarioRepository.findByLogin(username);
        userOpt.ifPresent(cliente::setUsuario);
        clienteService.salvarCliente(cliente);
        return "redirect:/clientes/listar";
    }

    @GetMapping("/excluir/{id}")
    public String excluirCliente(@PathVariable("id") Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if (clienteOpt.isPresent() && !clientePertenceAoUsuario(clienteOpt.get(), username)) {
            return "redirect:/acesso-negado";
        }

        clienteService.excluirCliente(id);
        return "redirect:/clientes/listar";
    }

    private boolean clientePertenceAoUsuario(Cliente cliente, String username) {
        return cliente.getUsuario() != null &&
                cliente.getUsuario().getLogin() != null &&
                cliente.getUsuario().getLogin().equals(username);
    }
}