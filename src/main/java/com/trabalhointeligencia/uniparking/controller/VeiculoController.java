package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Estacionamento;
import com.trabalhointeligencia.uniparking.models.Registro;
import com.trabalhointeligencia.uniparking.models.Vaga;
import com.trabalhointeligencia.uniparking.models.Veiculo;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import com.trabalhointeligencia.uniparking.services.ClienteService;
import com.trabalhointeligencia.uniparking.services.EstacionamentoService;
import com.trabalhointeligencia.uniparking.services.RegistroService;
import com.trabalhointeligencia.uniparking.services.ValoresService;
import com.trabalhointeligencia.uniparking.services.VagaService;
import com.trabalhointeligencia.uniparking.services.VeiculoService;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private ValoresService valoresService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("veiculo", new Veiculo());
        model.addAttribute("clientes", clienteService.listarClientesPorUsuario(username));
        List<Estacionamento> ests = estacionamentoService.listarEstacionamentos()
                .stream()
                .filter(est -> est.getUsuario() != null && est.getUsuario().getLogin().equals(username) && Boolean.TRUE.equals(est.getAtivo()))
                .collect(Collectors.toList());
        model.addAttribute("estacionamentos", ests);
        return "cadastroVeiculo";
    }

    @PostMapping
    public String salvarVeiculo(@Valid @ModelAttribute Veiculo veiculo, BindingResult result,
                                @RequestParam("estacionamentoId") Integer estacionamentoId,
                                @RequestParam(value = "numeroVaga", required = false) String numeroVaga,
                                RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            return "cadastroVeiculo";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Estacionamento> estOpt = estacionamentoService.buscarEstacionamentoPorId(estacionamentoId);
        if(estOpt.isEmpty() || !Boolean.TRUE.equals(estOpt.get().getAtivo()) ||
                estOpt.get().getUsuario() == null || !estOpt.get().getUsuario().getLogin().equals(username)){
            redirectAttributes.addFlashAttribute("errorMessage", "Estacionamento inválido ou excluído.");
            return "redirect:/veiculos/cadastrar";
        }
        try {
            Veiculo novoVeiculo = veiculoService.salvarVeiculo(veiculo);
            Vaga vagaSelecionada = null;
            if(numeroVaga != null && !numeroVaga.trim().isEmpty()){
                vagaSelecionada = new Vaga();
                vagaSelecionada.setNumeroVaga(numeroVaga);
                vagaSelecionada.setEstacionamento(estOpt.get());
                vagaSelecionada = vagaService.salvarVaga(vagaSelecionada);
            } else {
                List<Vaga> vagasDoEstacionamento = vagaService.listarVagas().stream()
                        .filter(v -> v.getEstacionamento().getIdEstacionamento().equals(estacionamentoId))
                        .collect(Collectors.toList());
                if(!vagasDoEstacionamento.isEmpty()){
                    vagaSelecionada = vagasDoEstacionamento.get(0);
                }
            }
            Registro registro = new Registro();
            registro.setDataEntrada(LocalDateTime.now());
            registro.setDataSaida(null);
            registro.setVeiculo(novoVeiculo);
            registro.setVaga(vagaSelecionada);
            registroService.salvarRegistro(registro);
            redirectAttributes.addFlashAttribute("successMessage", "Veículo cadastrado com sucesso!");
            return "redirect:/";
        } catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Falha ao cadastrar veículo: " + e.getMessage());
            return "redirect:/veiculos/cadastrar";
        }
    }

    @GetMapping("/listar")
    public String listarVeiculos(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Veiculo> veiculosAtivos = registroService.listarRegistros().stream()
                .filter(reg -> reg.getDataSaida() == null && reg.getVeiculo() != null
                        && reg.getVaga() != null
                        && reg.getVaga().getEstacionamento() != null
                        && reg.getVaga().getEstacionamento().getUsuario() != null
                        && reg.getVaga().getEstacionamento().getUsuario().getLogin().equals(username))
                .map(Registro::getVeiculo)
                .collect(Collectors.toList());
        int start = page * size;
        int end = Math.min(start + size, veiculosAtivos.size());
        List<Veiculo> veiculosPaginados = veiculosAtivos.subList(start, end);
        Page<Veiculo> veiculosPage = new PageImpl<>(veiculosPaginados, PageRequest.of(page, size), veiculosAtivos.size());
        model.addAttribute("veiculosPage", veiculosPage);
        return "vehicles";
    }

    @GetMapping("/buscar")
    public String buscarVeiculoPorPlaca(@RequestParam("placa") String placa, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Registro> registrosBusca = registroService.listarRegistros().stream()
                .filter(reg -> reg.getDataSaida() == null && reg.getVeiculo() != null
                        && reg.getVeiculo().getPlaca().toLowerCase().contains(placa.toLowerCase())
                        && reg.getVaga() != null
                        && reg.getVaga().getEstacionamento() != null
                        && reg.getVaga().getEstacionamento().getUsuario() != null
                        && reg.getVaga().getEstacionamento().getUsuario().getLogin().equals(username))
                .collect(Collectors.toList());
        model.addAttribute("registrosBusca", registrosBusca);
        model.addAttribute("searchPlaca", placa);
        return "buscaVeiculo";
    }

    @PostMapping("/checkout/{id}")
    public String checkoutVeiculo(@PathVariable("id") Integer veiculoId, RedirectAttributes redirectAttributes) {
        Optional<Registro> registroOpt = registroService.listarRegistros().stream()
                .filter(reg -> reg.getVeiculo() != null &&
                        reg.getVeiculo().getId().equals(veiculoId) &&
                        reg.getDataSaida() == null)
                .findFirst();
        if(registroOpt.isPresent()){
            Registro registro = registroOpt.get();
            LocalDateTime dataSaida = LocalDateTime.now();
            registro.setDataSaida(dataSaida);
            long duracao = Duration.between(registro.getDataEntrada(), dataSaida).toMinutes();
            registro.setDuracao((int)duracao);
            if(registro.getVaga() != null && registro.getVaga().getEstacionamento() != null){
                Integer estId = registro.getVaga().getEstacionamento().getIdEstacionamento();
                Optional<com.trabalhointeligencia.uniparking.models.Valores> valoresOpt = valoresService.buscarPorEstacionamentoId(estId);
                if(valoresOpt.isPresent()){
                    com.trabalhointeligencia.uniparking.models.Valores valores = valoresOpt.get();
                    BigDecimal valorcobranca = estacionamentoService.valorcobranca(
                            registro.getDataEntrada(),
                            dataSaida,
                            valores.getTempoMinimo(),
                            valores.getValorBase(),
                            valores.getValorAdicional()
                    );
                    registro.setValorCobrado(valorcobranca);
                }
            }
            registroService.salvarRegistro(registro);
            redirectAttributes.addFlashAttribute("successMessage", "Checkout feito com sucesso!");
        }
        return "redirect:/registros";
    }
}
