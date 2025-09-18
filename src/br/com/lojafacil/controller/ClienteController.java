// Arquivo: ClienteController.java
package br.com.lojafacil.controller;

import br.com.lojafacil.dao.interfaces.IClienteDAO;
import br.com.lojafacil.model.Cliente;
import java.util.Collections;
import java.util.List;

public class ClienteController {

    // O Controller depende da ABSTRAÇÃO (interface), e não da implementação concreta.
    // Isso é o "D" do SOLID: Princípio da Inversão de Dependência.
    private final IClienteDAO clienteDAO;

    // O DAO é fornecido de fora quando o Controller é criado.
    // Isso é a "Injeção de Dependência".
    public ClienteController(IClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    /**
     * Adiciona um novo cliente após validar as regras de negócio.
     *
     * @param cliente O objeto Cliente a ser adicionado.
     * @return Uma String com a mensagem de sucesso ou erro.
     */
    public String adicionarCliente(Cliente cliente) {
        // ---- REGRA DE NEGÓCIO 1: Validação de campos obrigatórios ----
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            return "Erro: O nome do cliente é obrigatório.";
        }
        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            return "Erro: O telefone do cliente é obrigatório.";
        }
        

        try {
            // Delega a responsabilidade de salvar no banco para o DAO
            clienteDAO.adicionar(cliente);
            return "Cliente adicionado com sucesso!";
        } catch (RuntimeException e) {
            // Captura o erro lançado pelo DAO e o transforma em uma mensagem 
            return "Erro ao adicionar cliente: " + e.getMessage();
        }
    }

    /**
     * Altera um cliente existente.
     *
     * @param cliente O objeto Cliente com os dados para alterar.
     * @return Uma String com a mensagem de sucesso ou erro.
     */
    public String alterarCliente(Cliente cliente) {
        // Validações semelhantes às de adicionar...
        if (cliente.getId() <= 0) {
            return "Erro: Cliente inválido para alteração.";
        }
         if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            return "Erro: O nome do cliente é obrigatório.";
        }
        
        try {
            clienteDAO.alterar(cliente);
            return "Cliente alterado com sucesso!";
        } catch (RuntimeException e) {
            return "Erro ao alterar cliente: " + e.getMessage();
        }
    }
    
    /**
     * Pesquisa clientes por nome.
     *
     * @param nome O nome a ser pesquisado.
     * @return Uma lista de clientes encontrados.
     */
    public List<Cliente> pesquisarClientes(String nome) {
        if(nome == null || nome.trim().isEmpty()){
            // Retorna uma lista vazia se a busca for inválida
            return Collections.emptyList();
        }
        
        try {
            return clienteDAO.pesquisar(nome);
        } catch (RuntimeException e) {
            // Por enquanto, retornamos uma lista vazia para não quebrar a aplicação.
            System.err.println("Erro ao pesquisar clientes: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
   
}
