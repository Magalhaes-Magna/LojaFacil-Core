// Arquivo: UsuarioDAO.java
package br.com.lojafacil.dao.impl;

import br.com.lojafacil.dao.interfaces.IUsuarioDAO;
import br.com.lojafacil.model.Usuario;
import br.com.lojafacil.util.ModuloConexao;
import java.sql.*;

public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM tbusuarios WHERE login = ? AND senha = ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, login);
            pst.setString(2, senha);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) { // Se encontrou um usuário com o login e senha
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("iduser"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setLogin(rs.getString("login"));
                    usuario.setPerfil(rs.getString("perfil"));
                    // Não retornamos a senha por segurança
                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage());
        }
        return null; // Retorna null se não encontrou o usuário ou a senha está incorreta
    }
    
  
}