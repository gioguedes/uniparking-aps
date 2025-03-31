package com.trabalhointeligencia.uniparking.services;

import com.trabalhointeligencia.uniparking.models.Valores;
import com.trabalhointeligencia.uniparking.repository.ValoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ValoresService {
    @Autowired
    private ValoresRepository valoresRepository;

    public Valores salvarValor(Valores valores) {
        return valoresRepository.save(valores);
    }

    public List<Valores> listarValores() {
        return valoresRepository.findAll();
    }

    public Optional<Valores> buscarPorEstacionamentoId(Integer estId) {
        return valoresRepository.findAll().stream()
                .filter(val -> val.getEstacionamento() != null && val.getEstacionamento().getIdEstacionamento().equals(estId))
                .findFirst();
    }
}
