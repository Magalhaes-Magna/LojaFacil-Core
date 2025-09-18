// Arquivo: IUsuarioDAO.java
package br.com.lojafacil.dao.interfaces;

import br.com.lojafacil.model.Usuario;

public interface IUsuarioDAO {
    
    /**
     * Verifica as credenciais no banco de dados.
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return Um objeto Usuario se as credenciais forem válidas, caso contrário, null.
     */
    Usuario autenticar(String login, String senha);
    
    // Podemos adicionar outros métodos para gerenciamento de usuários no futuro
    // void adicionar(Usuario usuario);
    // void alterar(Usuario usuario);
}