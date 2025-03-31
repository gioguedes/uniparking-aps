package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Estacionamento;
import com.trabalhointeligencia.uniparking.models.Valores;
import com.trabalhointeligencia.uniparking.models.Usuario;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import com.trabalhointeligencia.uniparking.services.EstacionamentoService;
import com.trabalhointeligencia.uniparking.services.ValoresService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private ValoresService valoresService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarEstacionamentos(Model model, @RequestParam("page") Optional<Integer> page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Estacionamento> all = estacionamentoService.listarEstacionamentos();
        List<Estacionamento> filtered = all.stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getLogin().equals(username))
                .toList();
        int currentPage = page.orElse(1);
        int pageSize = 10;
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        List<Estacionamento> subList = filtered.subList(start, end);
        Page<Estacionamento> estPage = new PageImpl<>(subList, PageRequest.of(currentPage - 1, pageSize), filtered.size());
        model.addAttribute("estPage", estPage);
        int totalPages = estPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("modo", "listar");
        return "estacionamento";
    }

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("estacionamento", new Estacionamento());
        model.addAttribute("modo", "cadastrar");
        return "estacionamento";
    }

    @PostMapping
    public String salvarEstacionamento(@Valid @ModelAttribute Estacionamento estacionamento, BindingResult result,
                                       @RequestParam("tempoMinimo") Integer tempoMinimo,
                                       @RequestParam("valorBase") String valorBase,
                                       @RequestParam("valorAdicional") String valorAdicional,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        if(estacionamento.getQntVagas() == null || estacionamento.getQntVagas() < 1){
            model.addAttribute("error", "A quantidade de vagas deve ser maior que 0.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        if(tempoMinimo == null || tempoMinimo < 1){
            model.addAttribute("error", "O tempo mínimo deve ser maior que 0.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        BigDecimal base = new BigDecimal(valorBase);
        if(base.compareTo(BigDecimal.ONE) < 0){
            model.addAttribute("error", "O valor base deve ser maior que 0.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        BigDecimal adicional = new BigDecimal(valorAdicional);
        if(adicional.compareTo(BigDecimal.ONE) < 0){
            model.addAttribute("error", "O valor adicional deve ser maior que 0.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        if(estacionamento.getNome() != null && estacionamento.getNome().length() > 100){
            model.addAttribute("error", "O nome do estacionamento deve ter no máximo 100 caracteres.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        if(estacionamento.getEndereco() != null && estacionamento.getEndereco().length() > 200){
            model.addAttribute("error", "O endereço deve ter no máximo 200 caracteres.");
            model.addAttribute("modo", "cadastrar");
            return "estacionamento";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> userOpt = usuarioRepository.findByLogin(username);
        userOpt.ifPresent(estacionamento::setUsuario);
        Estacionamento estSalvo = estacionamentoService.salvarEstacionamento(estacionamento);
        Valores valores = new Valores();
        valores.setEstacionamento(estSalvo);
        valores.setTempoMinimo(tempoMinimo);
        valores.setValorBase(base);
        valores.setValorAdicional(adicional);
        valoresService.salvarValor(valores);
        redirectAttributes.addFlashAttribute("success", "Estacionamento criado com sucesso!");
        return "redirect:/estacionamento";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable("id") Integer id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Estacionamento> opt = estacionamentoService.buscarEstacionamentoPorId(id);
        if(opt.isPresent()){
            Estacionamento estacionamento = opt.get();

            if (estacionamento.getUsuario() == null || !estacionamento.getUsuario().getLogin().equals(username)) {
                return "redirect:/acesso-negado";
            }

            model.addAttribute("estacionamento", estacionamento);
            model.addAttribute("modo", "editar");
            return "estacionamento";
        }
        return "redirect:/estacionamento";
    }

    @PostMapping("/editar")
    public String editarEstacionamento(@Valid @ModelAttribute Estacionamento estacionamento, BindingResult result,
                                       RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Estacionamento> originalOpt = estacionamentoService.buscarEstacionamentoPorId(estacionamento.getIdEstacionamento());
        if (!originalOpt.isPresent()) {
            return "redirect:/estacionamento";
        }

        Estacionamento original = originalOpt.get();
        if (original.getUsuario() == null || !original.getUsuario().getLogin().equals(username)) {
            return "redirect:/acesso-negado";
        }

        if(estacionamento.getNome() != null && estacionamento.getNome().length() > 100){
            result.rejectValue("nome", "error.estacionamento", "O nome do estacionamento deve ter no máximo 100 caracteres.");
        }
        if(estacionamento.getEndereco() != null && estacionamento.getEndereco().length() > 200){
            result.rejectValue("endereco", "error.estacionamento", "O endereço deve ter no máximo 200 caracteres.");
        }
        if(result.hasErrors()){
            return "estacionamento";
        }

        Optional<Usuario> userOpt = usuarioRepository.findByLogin(username);
        userOpt.ifPresent(estacionamento::setUsuario);

        if(Boolean.FALSE.equals(original.getAtivo())){
            estacionamento.setAtivo(false);
        }
        estacionamentoService.salvarEstacionamento(estacionamento);
        redirectAttributes.addFlashAttribute("success", "Estacionamento atualizado com sucesso!");
        return "redirect:/estacionamento";
    }

    @GetMapping("/excluir/{id}")
    public String excluirEstacionamento(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Estacionamento> estacionamentoOpt = estacionamentoService.buscarEstacionamentoPorId(id);
        if (estacionamentoOpt.isPresent()) {
            Estacionamento estacionamento = estacionamentoOpt.get();

            if (estacionamento.getUsuario() == null || !estacionamento.getUsuario().getLogin().equals(username)) {
                return "redirect:/acesso-negado";
            }

            estacionamentoService.excluirEstacionamento(id);
            redirectAttributes.addFlashAttribute("success", "Estacionamento excluído com sucesso!");
        }

        return "redirect:/estacionamento";
    }
}