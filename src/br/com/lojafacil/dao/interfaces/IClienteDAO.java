// Arquivo: IClienteDAO.java
package br.com.lojafacil.dao.interfaces;

import br.com.lojafacil.model.Cliente;
import java.util.List;

public interface IClienteDAO {
    void adicionar(Cliente cliente);
    void alterar(Cliente cliente);
    void remover(String id);
    List<Cliente> pesquisar(String nome);
    Cliente consultar(String id);
}