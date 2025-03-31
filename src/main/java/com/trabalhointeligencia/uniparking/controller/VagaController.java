package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Estacionamento;
import com.trabalhointeligencia.uniparking.models.Vaga;
import com.trabalhointeligencia.uniparking.services.EstacionamentoService;
import com.trabalhointeligencia.uniparking.services.RegistroService;
import com.trabalhointeligencia.uniparking.services.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vagas")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private RegistroService registroService;

    @GetMapping
    public String listarVagas(@RequestParam(value = "estacionamentoId", required = false) Integer estacionamentoId,
                              Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Estacionamento> allEst = estacionamentoService.listarEstacionamentos();
        List<Estacionamento> ests = allEst.stream()
                .filter(e -> e.getUsuario() != null && e.getUsuario().getLogin().equals(username))
                .collect(Collectors.toList());
        model.addAttribute("estacionamentos", ests);
        Integer estId = (estacionamentoId != null) ? estacionamentoId : (!ests.isEmpty() ? ests.get(0).getIdEstacionamento() : null);
        int totalVagas = 0;
        int vagasDisponiveis = 0;
        List<Vaga> vagas = List.of();
        if (estId != null) {
            Optional<Estacionamento> estOpt = ests.stream()
                    .filter(e -> e.getIdEstacionamento().equals(estacionamentoId))
                    .findFirst();
            if (estOpt.isPresent()) {
                totalVagas = estOpt.get().getQntVagas();
            }
            long activeRegistros = registroService.listarRegistros().stream()
                    .filter(reg -> reg.getDataSaida() == null)
                    .filter(reg -> reg.getVaga() != null && reg.getVaga().getEstacionamento() != null
                            && reg.getVaga().getEstacionamento().getIdEstacionamento().equals(estacionamentoId))
                    .count();
            vagasDisponiveis = totalVagas - (int) activeRegistros;
            vagas = vagaService.listarVagas().stream()
                    .filter(v -> v.getEstacionamento() != null && v.getEstacionamento().getIdEstacionamento().equals(estacionamentoId))
                    .collect(Collectors.toList());
        }
        model.addAttribute("totalVagas", totalVagas);
        model.addAttribute("vagasDisponiveis", vagasDisponiveis);
        model.addAttribute("vagas", vagas);
        model.addAttribute("estacionamentoId", estId);
        return "vagas";
    }
}
