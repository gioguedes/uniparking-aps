package com.trabalhointeligencia.uniparking.services;

import com.trabalhointeligencia.uniparking.models.Usuario;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("ROLE_USER");
        }

        return usuarioRepository.save(usuario);
    }

    public boolean usuarioExistente(String login) {
        return usuarioRepository.findByLogin(login).isPresent();
    }

    public Usuario buscarPorLogin(String login) {
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);
        return usuario.orElse(null);
    }
}