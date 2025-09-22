// Arquivo: VendaControllerTest.java 
package br.com.lojafacil.controller;

import br.com.lojafacil.dao.interfaces.*;
import br.com.lojafacil.model.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VendaControllerTest {

    @Mock
    private IVendaDAO vendaDAO;
    @Mock
    private IClienteDAO clienteDAO;
    @Mock
    private IProdutoDAO produtoDAO;
    
    @InjectMocks
    private VendaController vendaController;
    
    @Before
    public void setUp() {
        Cliente clienteFalso = new Cliente();
        clienteFalso.setId(1);
        clienteFalso.setNome("Cliente Teste");
        
        Produto produtoFalso = new Produto();
        produtoFalso.setId(10);
        produtoFalso.setNome("Produto Teste");
        produtoFalso.setQuantidadeEstoque(100);
        produtoFalso.setPrecoVenda(50.0);
        
        when(clienteDAO.consultar("1")).thenReturn(clienteFalso);
        when(produtoDAO.buscarPorId(10)).thenReturn(produtoFalso);
    }
    
    @Test
    public void deveRealizarVendaComSucessoSeEstoqueForSuficiente() {
        System.out.println("Testando venda com sucesso...");
        
        // Criamos um "carrinho de compras" como o método espera
        Map<Integer, Integer> carrinho = new HashMap<>();
        carrinho.put(10, 5); // Comprar 5 unidades do produto com ID 10
    
        // E passamos o ID do cliente (int) e o carrinho (Map)
        String resultado = vendaController.realizarVenda(1, carrinho);
        
        assertTrue(resultado.contains("sucesso"));
    }
    
    @Test
    public void naoDeveRealizarVendaSeEstoqueForInsuficiente() {
        System.out.println("Testando venda com estoque insuficiente...");

        // --- CORREÇÃO AQUI ---
        Map<Integer, Integer> carrinho = new HashMap<>();
        carrinho.put(10, 200); // Tenta comprar 200 unidades (só temos 100 no mock)
    
        String resultado = vendaController.realizarVenda(1, carrinho);
        
        assertEquals("Erro: Estoque insuficiente para o produto 'Produto Teste'. Disponível: 100", resultado);
    }
}