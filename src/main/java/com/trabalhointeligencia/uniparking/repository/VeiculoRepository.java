package com.trabalhointeligencia.uniparking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trabalhointeligencia.uniparking.models.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, Integer> {
}
