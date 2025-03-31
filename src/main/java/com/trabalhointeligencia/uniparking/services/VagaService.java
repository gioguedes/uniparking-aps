package com.trabalhointeligencia.uniparking.services;

import com.trabalhointeligencia.uniparking.models.Vaga;
import com.trabalhointeligencia.uniparking.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VagaService {
    @Autowired
    private VagaRepository vagaRepository;

    public Vaga salvarVaga(Vaga vaga) {
        return vagaRepository.save(vaga);
    }

    public List<Vaga> listarVagas() {
        return vagaRepository.findAll();
    }
}
