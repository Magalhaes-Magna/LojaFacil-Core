// Arquivo: ProdutoController.java
package br.com.lojafacil.controller;

import br.com.lojafacil.dao.interfaces.IProdutoDAO;
import br.com.lojafacil.model.Produto;
import java.util.Collections;
import java.util.List;

public class ProdutoController {

    private final IProdutoDAO produtoDAO;

    // A dependência da interface IProdutoDAO é injetada via construtor
    public ProdutoController(IProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    /**
     * Valida e adiciona um novo produto.
     * @param produto O produto a ser adicionado.
     * @return Uma String com a mensagem de sucesso ou erro.
     */
    public String adicionarProduto(Produto produto) {
        // --- Início das Regras de Negócio ---
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            return "Erro: O nome do produto é obrigatório.";
        }
        if (produto.getPrecoVenda() <= 0) {
            return "Erro: O preço de venda deve ser um valor positivo.";
        }
        if (produto.getQuantidadeEstoque() < 0) {
            return "Erro: A quantidade em estoque não pode ser negativa.";
        }
        // --- Fim das Regras de Negócio ---

        try {
            // Delega a responsabilidade de persistência para o DAO
            produtoDAO.adicionar(produto);
            return "Produto adicionado com sucesso!";
        } catch (RuntimeException e) {
            // Traduz a exceção técnica do DAO para uma mensagem amigável
            return "Erro ao salvar o produto no banco de dados: " + e.getMessage();
        }
    }

    /**
     * Valida e altera um produto existente.
     * @param produto O produto com os dados alterados.
     * @return Uma String com a mensagem de sucesso ou erro.
     */
    public String alterarProduto(Produto produto) {
        if(produto.getId() <= 0){
            return "Erro: Produto inválido para alteração.";
        }
        // Repete as mesmas validações do método adicionar
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            return "Erro: O nome do produto é obrigatório.";
        }
        if (produto.getPrecoVenda() <= 0) {
            return "Erro: O preço de venda deve ser um valor positivo.";
        }

        try {
            produtoDAO.alterar(produto);
            return "Produto alterado com sucesso!";
        } catch (RuntimeException e) {
            return "Erro ao alterar o produto: " + e.getMessage();
        }
    }
    
    /**
     * Busca produtos por parte do nome.
     * @param nome O nome a ser pesquisado.
     * @return Uma lista de produtos encontrados.
     */
    public List<Produto> buscarProdutos(String nome) {
        if(nome == null) {
           return Collections.emptyList(); // Retorna lista vazia para busca nula
        }
        try {
            return produtoDAO.buscarPorNome(nome);
        } catch (RuntimeException e) {
            System.err.println("Falha ao buscar produtos: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}