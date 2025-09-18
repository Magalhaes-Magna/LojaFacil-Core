// Arquivo: ClienteDAO.java
package br.com.lojafacil.dao.impl;

import br.com.lojafacil.dao.interfaces.IClienteDAO;
import br.com.lojafacil.model.Cliente;
import br.com.lojafacil.util.ModuloConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements IClienteDAO {

    @Override
    public void adicionar(Cliente cliente) {
        
        String sql = "INSERT INTO CLIENTES(cpf, nome, endereco, telefone, email) VALUES(?, ?, ?, ?, ?)";
        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setString(1, cliente.getCpf());
            pst.setString(2, cliente.getNome());
            pst.setString(3, cliente.getEndereco());
            pst.setString(4, cliente.getTelefone());
            pst.setString(5, cliente.getEmail());

            pst.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar cliente no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Cliente cliente) {
       
        String sql = "UPDATE CLIENTES SET cpf = ?, nome = ?, endereco = ?, telefone = ?, email = ? WHERE id_cliente = ?";
        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setString(1, cliente.getCpf());
            pst.setString(2, cliente.getNome());
            pst.setString(3, cliente.getEndereco());
            pst.setString(4, cliente.getTelefone());
            pst.setString(5, cliente.getEmail());
            pst.setInt(6, cliente.getId());

            int linhasAfetadas = pst.executeUpdate();
            
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Cliente não encontrado para alteração, ID: " + cliente.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar cliente no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public void remover(String id) {
    
        String sql = "DELETE FROM CLIENTES WHERE id_cliente = ?";
        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(sql)) {
            
            int idCliente = Integer.parseInt(id);
            pst.setInt(1, idCliente);
            pst.executeUpdate();

        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro: O ID fornecido para remoção não é um número válido.");
        } catch (SQLException e) {
             if (e.getSQLState().startsWith("23")) {
                 throw new RuntimeException("Não é possível remover o cliente pois ele possui vendas associadas.");
            }
            throw new RuntimeException("Erro ao remover cliente no banco de dados: " + e.getMessage());
        }
    }
    
    @Override
    public List<Cliente> pesquisar(String nome) {
        List<Cliente> listaClientes = new ArrayList<>();
      
        String sql = "SELECT * FROM CLIENTES WHERE nome LIKE ?";

        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setString(1, "%" + nome + "%");
            try(ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Cliente cliente = new Cliente();
                
                    cliente.setId(rs.getInt("id_cliente"));
                    cliente.setCpf(rs.getString("cpf"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setEndereco(rs.getString("endereco"));
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setEmail(rs.getString("email"));
                    listaClientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar clientes: " + e.getMessage());
        }
        return listaClientes;
    }
    
    @Override
    public Cliente consultar(String id) {
       
        String sql = "SELECT * FROM CLIENTES WHERE id_cliente = ?";
        try (Connection conexao = ModuloConexao.conector();
             PreparedStatement pst = conexao.prepareStatement(sql)) {
            
            int idCliente = Integer.parseInt(id);
            pst.setInt(1, idCliente);

            try(ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
               
                    cliente.setId(rs.getInt("id_cliente"));
                    cliente.setCpf(rs.getString("cpf"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setEndereco(rs.getString("endereco"));
                    cliente.setTelefone(rs.getString("telefone"));
                    cliente.setEmail(rs.getString("email"));
                    return cliente;
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro: O ID fornecido para consulta não é um número válido.");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar cliente: " + e.getMessage());
        }
        return null;
    }
}