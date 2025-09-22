// Arquivo: VendaTest.java 
package br.com.lojafacil.model;

// ---Imports ---
import org.junit.Test;
import static org.junit.Assert.*;

public class VendaTest {

    @Test 
    public void testGetValorTotal() {
        System.out.println("Testando o cálculo do valor total da venda (JUnit 4)...");
        
        Venda venda = new Venda();
        
        ItemVenda item1 = new ItemVenda();
        item1.setQuantidade(2);
        item1.setPrecoVendaMomento(10.00);
        
        ItemVenda item2 = new ItemVenda();
        item2.setQuantidade(1);
        item2.setPrecoVendaMomento(30.00);
        
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);
        
        double valorEsperado = 50.00;
        double valorCalculado = venda.getValorTotal();
        
        assertEquals(valorEsperado, valorCalculado, 0.01);
    }
}