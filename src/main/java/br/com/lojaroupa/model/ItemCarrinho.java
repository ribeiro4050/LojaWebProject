package br.com.lojaroupa.model;
// Certifique-se de que esta classe importa sua classe Produto
// Se a classe Produto estiver no mesmo pacote, esta linha pode não ser necessária,
// mas é bom ter certeza.
import java.math.BigDecimal;    // <<<< FALTA ESTE IMPORT!
import java.math.RoundingMode;

public class ItemCarrinho {
    
    // A referência ao produto adicionado
    private Produto produto;
    
    // A quantidade desse produto no carrinho
    private int quantidade;
    
    // Construtor: Usado para criar um novo item no carrinho
    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // --- Getters e Setters ---
    
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
    
    // --- Método de Lógica (Cálculo) ---
    
    /**
     * Calcula o valor total deste item (preço unitário * quantidade).
     * @return O subtotal do item.
     */
    public BigDecimal getSubtotal() {
        // Multiplica o preço (BigDecimal) pela quantidade (int) e retorna BigDecimal
        return this.produto.getPreco().multiply(BigDecimal.valueOf(this.quantidade))
                   .setScale(2, RoundingMode.HALF_UP); // Define 2 casas decimais
    }
}