// Arquivo: VendaDAO.java
package br.com.lojafacil.dao.impl;

import br.com.lojafacil.dao.interfaces.IProdutoDAO;
import br.com.lojafacil.dao.interfaces.IVendaDAO;
import br.com.lojafacil.dao.interfaces.IClienteDAO;
import br.com.lojafacil.model.ItemVenda;
import br.com.lojafacil.model.Venda;
import br.com.lojafacil.util.ModuloConexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO implements IVendaDAO {

    // VendaDAO precisa de ProdutoDAO para dar baixa no estoque.
    // Usamos a mesma técnica de Injeção de Dependência que usamos nos Controllers.
    private final IProdutoDAO produtoDAO;
    private final IClienteDAO clienteDAO;

    public VendaDAO(IProdutoDAO produtoDAO, IClienteDAO clienteDAO) {
        this.produtoDAO = produtoDAO;
        this.clienteDAO = clienteDAO;
    }

    @Override
    public Venda registrarVenda(Venda venda) {
        Connection conn = null;
        String sqlVenda = "INSERT INTO VENDAS (id_cliente) VALUES (?)";
        String sqlItemVenda = "INSERT INTO ITENS_VENDA (id_venda, id_produto, quantidade, preco_venda_momento) VALUES (?, ?, ?, ?)";

        try {
            // PONTO 1: Obtém a conexão
            conn = ModuloConexao.conector();

            // PONTO 2: Desliga o auto-commit para iniciar a TRANSAÇÃO
            conn.setAutoCommit(false);

            // --- ETAPA A: Inserir na tabela VENDAS ---
            try (PreparedStatement pstVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                pstVenda.setInt(1, venda.getCliente().getId());
                pstVenda.executeUpdate();

                // Recupera o ID da venda que acabou de ser gerado
                try (ResultSet rs = pstVenda.getGeneratedKeys()) {
                    if (rs.next()) {
                        venda.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Falha ao obter o ID da venda, nenhuma chave gerada.");
                    }
                }
            }

            // --- ETAPA B: Inserir na tabela ITENS_VENDA e dar baixa no estoque ---
            for (ItemVenda item : venda.getItens()) {
                // Insere o item na tabela ITENS_VENDA
                try (PreparedStatement pstItem = conn.prepareStatement(sqlItemVenda)) {
                    pstItem.setInt(1, venda.getId()); // Usa o ID da venda gerado acima
                    pstItem.setInt(2, item.getProduto().getId());
                    pstItem.setInt(3, item.getQuantidade());
                    pstItem.setDouble(4, item.getPrecoVendaMomento());
                    pstItem.executeUpdate();
                }

                // Dá baixa no estoque do produto usando o ProdutoDAO
                // A mesma conexão da transação é passada para garantir a atomicidade.
                produtoDAO.baixarEstoque(item.getProduto().getId(), item.getQuantidade(), conn);
            }

            // PONTO 3: Se tudo deu certo até aqui, efetiva a TRANSAÇÃO
            conn.commit();

        } catch (SQLException | RuntimeException e) {
            // PONTO 4: Se qualquer erro ocorreu, desfaz a TRANSAÇÃO
            try {
                if (conn != null) {
                    System.err.println("Transação da venda está sendo desfeita (rollback)!");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                // Erro grave ao tentar fazer o rollback
                throw new RuntimeException("Erro crítico ao tentar reverter a transação da venda: " + ex.getMessage());
            }
            // Lança a exceção original para o Controller saber que falhou
            throw new RuntimeException("Erro ao registrar a venda: " + e.getMessage());

        } finally {
            // PONTO 5: Garante que a conexão volte ao normal e seja fechada
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Devolve a conexão ao modo padrão
                    conn.close();
                }
            } catch (SQLException e) {
                // Erro ao fechar a conexão, apenas logar.
                System.err.println("Erro ao fechar a conexão após a transação: " + e.getMessage());
            }
        }

        return venda; // Retorna o objeto venda com o ID
    }

    // --- Outros Métodos ---
    @Override
    public Venda buscarPorId(int idVenda) {
        String sqlVenda = "SELECT * FROM VENDAS WHERE id_venda = ?";
        String sqlItens = "SELECT * FROM ITENS_VENDA WHERE id_venda = ?";
        Venda venda = null;

        try (Connection conn = ModuloConexao.conector()) {
            // 1. Busca os dados principais da Venda
            try (PreparedStatement pstVenda = conn.prepareStatement(sqlVenda)) {
                pstVenda.setInt(1, idVenda);
                try (ResultSet rsVenda = pstVenda.executeQuery()) {
                    if (rsVenda.next()) {
                        venda = new Venda();
                        venda.setId(rsVenda.getInt("id_venda"));
                        venda.setDataVenda(rsVenda.getTimestamp("data_venda").toLocalDateTime());

                        // Busca o objeto Cliente completo usando o ClienteDAO
                        int idCliente = rsVenda.getInt("id_cliente");
                        venda.setCliente(clienteDAO.consultar(String.valueOf(idCliente)));
                    }
                }
            }

            // 2. Se a venda foi encontrada, busca seus itens
            if (venda != null) {
                try (PreparedStatement pstItens = conn.prepareStatement(sqlItens)) {
                    pstItens.setInt(1, idVenda);
                    try (ResultSet rsItens = pstItens.executeQuery()) {
                        while (rsItens.next()) {
                            ItemVenda item = new ItemVenda();
                            item.setId(rsItens.getInt("id_item_venda"));
                            item.setQuantidade(rsItens.getInt("quantidade"));
                            item.setPrecoVendaMomento(rsItens.getDouble("preco_venda_momento"));

                            // Busca o objeto Produto completo usando o ProdutoDAO
                            int idProduto = rsItens.getInt("id_produto");
                            item.setProduto(produtoDAO.buscarPorId(idProduto));

                            venda.adicionarItem(item);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar venda por ID: " + e.getMessage());
        }

        return venda;
    }

    @Override
    public List<Venda> listarVendasPorCliente(int idCliente) {
        String sql = "SELECT id_venda FROM VENDAS WHERE id_cliente = ?";
        List<Venda> vendas = new ArrayList<>();
        try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idCliente);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // Para cada ID de venda encontrado, usamos o método que já criamos
                    // para buscar o objeto Venda completo.
                    int idVenda = rs.getInt("id_venda");
                    vendas.add(buscarPorId(idVenda));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas por cliente: " + e.getMessage());
        }
        return vendas;
    }

    @Override
    public List<Venda> listarTodas() {
        String sql = "SELECT id_venda FROM VENDAS ORDER BY data_venda DESC";
        List<Venda> vendas = new ArrayList<>();
        try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int idVenda = rs.getInt("id_venda");
                vendas.add(buscarPorId(idVenda));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todas as vendas: " + e.getMessage());
        }
        return vendas;
    }

}
