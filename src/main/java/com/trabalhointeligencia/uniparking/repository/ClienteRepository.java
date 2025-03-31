package com.trabalhointeligencia.uniparking.repository;

import com.trabalhointeligencia.uniparking.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByAtivoTrueAndUsuario_Login(String login);
}
