// Arquivo: IProdutoDAO.java (VERSÃO ATUALIZADA)
package br.com.lojafacil.dao.interfaces;

import br.com.lojafacil.model.Produto;
import java.sql.Connection; // Import necessário para o método de baixar estoque
import java.util.List;

public interface IProdutoDAO {
    void adicionar(Produto produto);
    void alterar(Produto produto);
    void remover(int idProduto);
    Produto buscarPorId(int idProduto);
    List<Produto> buscarPorNome(String nome);
    List<Produto> listarTodos();
   
    void baixarEstoque(int idProduto, int quantidadeVendida, Connection conn);
    void adicionarEstoque(int idProduto, int quantidadeAdicionar);
}