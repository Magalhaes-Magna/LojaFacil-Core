package br.com.lojafacil.application;

// Imports para todas as entidades
import br.com.lojafacil.controller.ClienteController;
import br.com.lojafacil.controller.ProdutoController;
import br.com.lojafacil.controller.UsuarioController;
import br.com.lojafacil.controller.VendaController;
import br.com.lojafacil.dao.impl.ClienteDAO;
import br.com.lojafacil.dao.impl.ProdutoDAO;
import br.com.lojafacil.dao.impl.UsuarioDAO;
import br.com.lojafacil.dao.impl.VendaDAO;
import br.com.lojafacil.dao.interfaces.IClienteDAO;
import br.com.lojafacil.dao.interfaces.IProdutoDAO;
import br.com.lojafacil.dao.interfaces.IUsuarioDAO;
import br.com.lojafacil.dao.interfaces.IVendaDAO;
import br.com.lojafacil.model.Cliente;
import br.com.lojafacil.model.Produto;
import br.com.lojafacil.model.Usuario;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        
        // --- BLOCO DE INSTANCIAÇÃO ÚNICA ---
        
        IClienteDAO clienteDAO = new ClienteDAO();
        IProdutoDAO produtoDAO = new ProdutoDAO();
        IUsuarioDAO usuarioDAO = new UsuarioDAO();
        IVendaDAO vendaDAO = new VendaDAO(produtoDAO, clienteDAO); 

        ClienteController clienteController = new ClienteController(clienteDAO);
        ProdutoController produtoController = new ProdutoController(produtoDAO);
        UsuarioController usuarioController = new UsuarioController(usuarioDAO);
        VendaController vendaController = new VendaController(vendaDAO, clienteDAO, produtoDAO);

        // --- FIM DO BLOCO DE INSTANCIAÇÃO ---

        
        System.out.println("--- INICIANDO TESTES DA CAMADA DE NEGÓCIO ---");
        
        // --- BLOCO DE TESTES PARA CLIENTE ---
        System.out.println("\n[TESTE CLIENTE 1] Tentando adicionar um cliente válido...");
        Cliente novoCliente = new Cliente();
        novoCliente.setCpf("987.654.321-00"); // CPF diferente para evitar o erro de duplicidade
        novoCliente.setNome("Joana Mendes");
        novoCliente.setEndereco("Avenida Brasil, 456");
        novoCliente.setTelefone("21988776655");
        novoCliente.setEmail("joana.mendes@email.com");
        String resultadoAdicao = clienteController.adicionarCliente(novoCliente);
        System.out.println("Resultado: " + resultadoAdicao);
        
        System.out.println("\n[TESTE CLIENTE 2] Tentando adicionar um cliente inválido...");
        Cliente clienteInvalido = new Cliente();
        clienteInvalido.setTelefone("1155554444");
        String resultadoInvalido = clienteController.adicionarCliente(clienteInvalido);
        System.out.println("Resultado: " + resultadoInvalido);
        
        
        // --- BLOCO DE TESTES PARA PRODUTO ---
        System.out.println("\n\n--- INICIANDO TESTES DA FUNCIONALIDADE DE PRODUTOS ---");
        System.out.println("\n[TESTE PRODUTO 1] Adicionando um produto válido...");
        Produto novoProduto = new Produto();
        novoProduto.setNome("Cadeira de Escritório");
        novoProduto.setDescricao("Ergonômica, cor preta");
        novoProduto.setPrecoVenda(550.00);
        novoProduto.setQuantidadeEstoque(15);
        String resultadoProd = produtoController.adicionarProduto(novoProduto);
        System.out.println("Resultado: " + resultadoProd);
        
        
        // --- BLOCO DE TESTES PARA LOGIN ---
        System.out.println("\n\n--- INICIANDO TESTES DA FUNCIONALIDADE DE LOGIN ---");
        System.out.println("\n[TESTE LOGIN 1] Tentando logar com usuário 'admin' e senha correta...");
        Usuario usuarioLogado = usuarioController.fazerLogin("admin", "admin123");
        if (usuarioLogado != null) {
            System.out.println("Resultado: Sucesso! Bem-vindo, " + usuarioLogado.getNome());
        } else {
            System.out.println("Resultado: Falha! Login ou senha incorretos.");
        }

        
        // --- BLOCO DE TESTES PARA VENDA ---
        System.out.println("\n\n--- INICIANDO TESTES DA FUNCIONALIDADE DE VENDA ---");
        Map<Integer, Integer> carrinho = new java.util.HashMap<>();
        carrinho.put(1, 1); // 1 unidade do produto com ID 1
        System.out.println("\n[TESTE VENDA 1] Registrando uma venda válida...");
        String resultadoVenda = vendaController.realizarVenda(2, carrinho); // Para a cliente Ana Silva (ID 2)
        System.out.println("Resultado: " + resultadoVenda);
        
        System.out.println("\n--- TESTES FINALIZADOS ---");
    }
}
