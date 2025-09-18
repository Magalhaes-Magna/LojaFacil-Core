// Arquivo: UsuarioController.java
package br.com.lojafacil.controller;

import br.com.lojafacil.dao.interfaces.IUsuarioDAO;
import br.com.lojafacil.model.Usuario;

public class UsuarioController {
    
    private final IUsuarioDAO usuarioDAO;

    public UsuarioController(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    
    /**
     * Tenta realizar o login de um usuário.
     * @param login O login fornecido.
     * @param senha A senha fornecida.
     * @return O objeto Usuario em caso de sucesso, ou null em caso de falha.
     */
    public Usuario fazerLogin(String login, String senha) {
        // Regra de Negócio: Login e senha não podem ser vazios
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            // Em um sistema web, poderíamos lançar uma exceção aqui para ser tratada
            // e mostrar uma mensagem de erro específica.
            return null;
        }
        
        try {
            return usuarioDAO.autenticar(login, senha);
        } catch (RuntimeException e) {
            System.err.println("Falha no login: " + e.getMessage());
            return null;
        }
    }
}