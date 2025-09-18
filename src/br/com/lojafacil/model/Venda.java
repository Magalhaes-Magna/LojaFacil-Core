// Arquivo: Venda.java
package br.com.lojafacil.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int id;
    
    // Assim como em ItemVenda, guardamos o objeto Cliente completo.
    private Cliente cliente;
    
    // Usamos a classe LocalDateTime, para trabalhar com data e hora.
    private LocalDateTime dataVenda;
    
    // Uma Venda TEM UMA LISTA de ItensVenda. É assim que modelamos a relação 1-para-Muitos.
    private List<ItemVenda> itens;

    // Construtor para inicializar a lista e evitar erros (NullPointerException)
    public Venda() {
        this.itens = new ArrayList<>();
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
    
    // --- Métodos de Negócio ---
    
    /**
     * Adiciona um item à lista de itens da venda.
     * @param item O ItemVenda a ser adicionado.
     */
    public void adicionarItem(ItemVenda item) {
        this.itens.add(item);
    }
    
    /**
     * Calcula e retorna o valor total da venda.
     * @return O valor total.
     */
    public double getValorTotal() {
        double total = 0.0;
        for (ItemVenda item : this.itens) {
            total += item.getQuantidade() * item.getPrecoVendaMomento();
        }
        return total;
    }
}