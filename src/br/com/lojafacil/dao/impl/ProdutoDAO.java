// Arquivo: ProdutoDAO.java (VERSÃO REFATORADA E ADAPTADA)
package br.com.lojafacil.dao.impl;

import br.com.lojafacil.dao.interfaces.IProdutoDAO;
import br.com.lojafacil.model.Produto;
import br.com.lojafacil.util.ModuloConexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements IProdutoDAO {

    // <-- MUDANÇA: Nome 'inserir' virou 'adicionar', retorno 'boolean' virou 'void'
    @Override
    public void adicionar(Produto produto) {
        String sql = "INSERT INTO PRODUTOS (nome, descricao, quantidade_estoque, preco_custo, preco_venda, estoque_minimo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getQuantidadeEstoque());
            pstmt.setDouble(4, produto.getPrecoCusto());
            pstmt.setDouble(5, produto.getPrecoVenda());
            pstmt.setInt(6, produto.getEstoqueMinimo());

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produto.setId(generatedKeys.getInt(1)); // Define o ID gerado de volta no objeto
                    }
                }
            }
        } catch (SQLException e) {
            // <-- MUDANÇA: Lança exceção em vez de imprimir no console
            throw new RuntimeException("Erro ao adicionar produto: " + e.getMessage());
        }
    }

    // <-- MUDANÇA: Nome 'atualizar' virou 'alterar', retorno 'boolean' virou 'void'
    @Override
    public void alterar(Produto produto) {
        String sql = "UPDATE PRODUTOS SET nome = ?, descricao = ?, quantidade_estoque = ?, preco_custo = ?, preco_venda = ?, estoque_minimo = ? WHERE id_produto = ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setInt(3, produto.getQuantidadeEstoque());
            pstmt.setDouble(4, produto.getPrecoCusto());
            pstmt.setDouble(5, produto.getPrecoVenda());
            pstmt.setInt(6, produto.getEstoqueMinimo());
            pstmt.setInt(7, produto.getId());

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Produto não encontrado para alteração, ID: " + produto.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar produto: " + e.getMessage());
        }
    }

    // <-- MUDANÇA: Nome 'excluir' virou 'remover', retorno 'boolean' virou 'void'
    @Override
    public void remover(int id) {
        String sql = "DELETE FROM PRODUTOS WHERE id_produto = ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                 throw new RuntimeException("Não é possível remover o produto pois ele está associado a vendas.");
            }
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage());
        }
    }

    @Override
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM PRODUTOS WHERE id_produto = ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaProduto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID: " + e.getMessage());
        }
        return null;
    }

    // <-- MUDANÇA: Implementando o buscarPorNome da interface
    @Override
    public List<Produto> buscarPorNome(String nome) {
        List<Produto> listaProdutos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUTOS WHERE nome LIKE ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    listaProdutos.add(mapearResultSetParaProduto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por nome: " + e.getMessage());
        }
        return listaProdutos;
    }

    @Override
    public List<Produto> listarTodos() {
        List<Produto> listaProdutos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUTOS ORDER BY nome";
        try (Connection conn = ModuloConexao.conector();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listaProdutos.add(mapearResultSetParaProduto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os produtos: " + e.getMessage());
        }
        return listaProdutos;
    }
    
    // <-- MUDANÇA: Mantivemos a lógica, só ajustamos retorno e tratamento de erro
    @Override
    public void baixarEstoque(int idProduto, int quantidadeVendida, Connection conn) {
        String sql = "UPDATE PRODUTOS SET quantidade_estoque = quantidade_estoque - ? WHERE id_produto = ? AND quantidade_estoque >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantidadeVendida);
            pstmt.setInt(2, idProduto);
            pstmt.setInt(3, quantidadeVendida);

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Estoque insuficiente para o produto ID " + idProduto + " ou produto não encontrado.");
            }
        } catch(SQLException e) {
            throw new RuntimeException("Erro ao baixar estoque: " + e.getMessage());
        }
    }

    // <-- MUDANÇA: Mantivemos a lógica, só ajustamos retorno e tratamento de erro
    @Override
    public void adicionarEstoque(int idProduto, int quantidadeAdicionar) {
        if (quantidadeAdicionar <= 0) {
            throw new IllegalArgumentException("A quantidade a adicionar ao estoque deve ser positiva.");
        }
        String sql = "UPDATE PRODUTOS SET quantidade_estoque = quantidade_estoque + ? WHERE id_produto = ?";
        try (Connection conn = ModuloConexao.conector();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantidadeAdicionar);
            pstmt.setInt(2, idProduto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    // <-- NOVO MÉTODO PRIVADO: Para evitar repetição de código
    private Produto mapearResultSetParaProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        produto.setPrecoCusto(rs.getDouble("preco_custo"));
        produto.setPrecoVenda(rs.getDouble("preco_venda"));
        produto.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        return produto;
    }
}