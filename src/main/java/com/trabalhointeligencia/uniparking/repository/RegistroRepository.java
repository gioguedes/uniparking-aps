package com.trabalhointeligencia.uniparking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trabalhointeligencia.uniparking.models.Registro;

public interface RegistroRepository extends JpaRepository<Registro, Integer> {
}
