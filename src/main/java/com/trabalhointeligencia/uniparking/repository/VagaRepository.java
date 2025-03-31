package com.trabalhointeligencia.uniparking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trabalhointeligencia.uniparking.models.Vaga;

public interface VagaRepository extends JpaRepository<Vaga, Integer> {
}
