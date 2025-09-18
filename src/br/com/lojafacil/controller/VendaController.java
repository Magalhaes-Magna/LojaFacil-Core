// Arquivo: VendaController.java
package br.com.lojafacil.controller;

import br.com.lojafacil.dao.interfaces.IClienteDAO;
import br.com.lojafacil.dao.interfaces.IProdutoDAO;
import br.com.lojafacil.dao.interfaces.IVendaDAO;
import br.com.lojafacil.model.Cliente;
import br.com.lojafacil.model.ItemVenda;
import br.com.lojafacil.model.Produto;
import br.com.lojafacil.model.Venda;
import java.util.List;
import java.util.Map;

public class VendaController {

    private final IVendaDAO vendaDAO;
    private final IClienteDAO clienteDAO;
    private final IProdutoDAO produtoDAO;

    // Injetamos todos os DAOs de que o Controller precisa para trabalhar.
    public VendaController(IVendaDAO vendaDAO, IClienteDAO clienteDAO, IProdutoDAO produtoDAO) {
        this.vendaDAO = vendaDAO;
        this.clienteDAO = clienteDAO;
        this.produtoDAO = produtoDAO;
    }

    /**
     * Orquestra todo o processo de realização de uma nova venda.
     *
     * @param idCliente O ID do cliente que está comprando.
     * @param itensCarrinho Um Mapa onde a chave é o ID do produto e o valor é a quantidade.
     * @return Uma String com a mensagem de sucesso ou erro.
     */
    public String realizarVenda(int idCliente, Map<Integer, Integer> itensCarrinho) {
        // --- Início das Regras de Negócio de Alto Nível ---
        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            return "Erro: O carrinho de compras não pode estar vazio.";
        }

        // 1. Busca o cliente no banco de dados.
        Cliente cliente = clienteDAO.consultar(String.valueOf(idCliente));
        if (cliente == null) {
            return "Erro: Cliente com ID " + idCliente + " não encontrado.";
        }
        
        Venda novaVenda = new Venda();
        novaVenda.setCliente(cliente);

        // 2. Itera sobre o carrinho para validar e criar os Itens da Venda.
        for (Map.Entry<Integer, Integer> entry : itensCarrinho.entrySet()) {
            int idProduto = entry.getKey();
            int quantidadeDesejada = entry.getValue();

            Produto produto = produtoDAO.buscarPorId(idProduto);
            if (produto == null) {
                return "Erro: Produto com ID " + idProduto + " não encontrado.";
            }

            // Regra de Negócio: Verifica se há estoque suficiente
            if (produto.getQuantidadeEstoque() < quantidadeDesejada) {
                return "Erro: Estoque insuficiente para o produto '" + produto.getNome() + "'. Disponível: " + produto.getQuantidadeEstoque();
            }

            // Se tudo estiver OK, cria o ItemVenda
            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(quantidadeDesejada);
            item.setPrecoVendaMomento(produto.getPrecoVenda()); // "Congela" o preço
            
            novaVenda.adicionarItem(item);
        }

        // 3. Se todas as validações passaram, tenta registrar a venda.
        try {
            vendaDAO.registrarVenda(novaVenda);
            return "Venda #" + novaVenda.getId() + " registrada com sucesso! Valor total: R$" + String.format("%.2f", novaVenda.getValorTotal());
        } catch (RuntimeException e) {
            return "Erro ao registrar a venda: " + e.getMessage();
        }
    }
    
    public List<Venda> listarVendasDoCliente(int idCliente) {
        return vendaDAO.listarVendasPorCliente(idCliente);
    }
}
