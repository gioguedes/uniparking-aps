package com.trabalhointeligencia.uniparking.repository;

import com.trabalhointeligencia.uniparking.models.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Integer> {
    List<Estacionamento> findByUsuario_IdUsuario(Integer idUsuario);
}
