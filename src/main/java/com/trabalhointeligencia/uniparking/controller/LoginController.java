package com.trabalhointeligencia.uniparking.controller;

import com.trabalhointeligencia.uniparking.models.Usuario;
import com.trabalhointeligencia.uniparking.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/cadastrar")
    public String exibirCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastroUsuario";
    }

    @PostMapping("/login/cadastrar")
    public String cadastrarUsuario(@ModelAttribute Usuario usuario, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.usuarioExistente(usuario.getLogin())) {
                model.addAttribute("errorMessage", "Este nome de usuário já está em uso. Por favor, escolha outro.");
                return "cadastroUsuario";
            }

            usuarioService.salvarUsuario(usuario);

            redirectAttributes.addAttribute("registered", true);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao cadastrar usuário: " + e.getMessage());
            return "cadastroUsuario";
        }
    }
}