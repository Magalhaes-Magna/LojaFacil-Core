// Arquivo: IVendaDAO.java
package br.com.lojafacil.dao.interfaces;

import br.com.lojafacil.model.Venda;
import java.util.List;

/**
 * Contrato para as operações de persistência da entidade Venda.
 */
public interface IVendaDAO {

    /**
     * Registra uma nova venda no banco de dados.
     * Esta operação deve ser transacional. Ela irá:
     * 1. Inserir um registro na tabela VENDAS.
     * 2. Inserir um ou mais registros na tabela ITENS_VENDA.
     * 3. Atualizar (dar baixa) no estoque de cada produto na tabela PRODUTOS.
     *
     * @param venda O objeto Venda, já contendo o Cliente e a lista de Itens.
     * @return O objeto Venda com o ID gerado pelo banco de dados.
     */
    Venda registrarVenda(Venda venda);

    /**
     * Busca uma venda específica pelo seu ID.
     * @param idVenda O ID da venda a ser procurada.
     * @return Um objeto Venda completo (com cliente e lista de itens), ou null se não for encontrada.
     */
    Venda buscarPorId(int idVenda);

    /**
     * Lista todas as vendas de um cliente específico.
     * @param idCliente O ID do cliente.
     * @return Uma lista de Vendas (pode estar vazia).
     */
    List<Venda> listarVendasPorCliente(int idCliente);

    /**
     * Lista todas as vendas registradas no sistema.
     * @return Uma lista de todas as Vendas (pode estar vazia).
     */
    List<Venda> listarTodas();
}
