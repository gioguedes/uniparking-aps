package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Estacionamento;
import com.trabalhointeligencia.uniparking.models.Registro;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import com.trabalhointeligencia.uniparking.services.EstacionamentoService;
import com.trabalhointeligencia.uniparking.services.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private RegistroService registroService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Estacionamento> estacionamentos = estacionamentoService.listarEstacionamentos()
                .stream()
                .filter(est -> est.getUsuario() != null && est.getUsuario().getLogin().equals(username) && Boolean.TRUE.equals(est.getAtivo()))
                .collect(Collectors.toList());
        Map<Integer, Integer> vagasDisponiveisMap = new HashMap<>();
        for (Estacionamento est : estacionamentos) {
            int totalVagas = est.getQntVagas();
            long vagasOcupadas = registroService.listarRegistros().stream()
                    .filter(reg -> reg.getDataSaida() == null
                            && reg.getVaga() != null
                            && reg.getVaga().getEstacionamento() != null
                            && reg.getVaga().getEstacionamento().getIdEstacionamento().equals(est.getIdEstacionamento()))
                    .count();
            int disponiveis = totalVagas - (int) vagasOcupadas;
            vagasDisponiveisMap.put(est.getIdEstacionamento(), disponiveis);
        }
        List<Registro> registrosAtivos = registroService.listarRegistros().stream()
                .filter(reg -> reg.getDataSaida() == null && reg.getVeiculo() != null
                        && reg.getVaga() != null
                        && reg.getVaga().getEstacionamento() != null
                        && reg.getVaga().getEstacionamento().getUsuario() != null
                        && reg.getVaga().getEstacionamento().getUsuario().getLogin().equals(username))
                .collect(Collectors.toList());
        model.addAttribute("estacionamentos", estacionamentos);
        model.addAttribute("vagasDisponiveisMap", vagasDisponiveisMap);
        model.addAttribute("registrosAtivos", registrosAtivos);
        return "index";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}