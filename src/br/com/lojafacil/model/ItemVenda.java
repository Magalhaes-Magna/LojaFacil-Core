// Arquivo: ItemVenda.java
package br.com.lojafacil.model;

public class ItemVenda {

    private int id;
    
    // Em vez de guardarmos apenas o id_produto, guardamos o objeto Produto inteiro.
    // Isso torna nosso modelo mais rico e fácil de usar.
    private Produto produto;
    
    private int quantidade;
    
    // "Congela" o preço do produto no momento da venda, garantindo a precisão histórica.
    private double precoVendaMomento;

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoVendaMomento() {
        return precoVendaMomento;
    }

    public void setPrecoVendaMomento(double precoVendaMomento) {
        this.precoVendaMomento = precoVendaMomento;
    }
}