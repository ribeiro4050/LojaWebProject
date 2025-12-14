package br.com.lojaroupa.model;

import java.math.BigDecimal;

public class Produto {
    private int idProduto;
    private String nome;
    private String marca;
    private String modelo;
    private int idCategoria;
    private String descricao;
    private String unidadeMedida;
    private String cor;
    private String tamanho; // VARCHAR no banco
    private BigDecimal preco; // Nome da coluna correto no banco
    private int estoqueMinimo;
    private String ativo;
    
    // NOVO ATRIBUTO: Caminho da Imagem
    private String caminhoImagem; 

    public Produto() {}

    public Produto(int idProduto, String nome, String marca, String modelo, int idCategoria, String descricao,
                   String unidadeMedida, String cor, String tamanho, BigDecimal preco, int estoqueMinimo, String ativo,
                   String caminhoImagem) { // NOVO PARÂMETRO NO CONSTRUTOR
        this.idProduto = idProduto;
        this.nome = nome;
        this.marca = marca;
        this.modelo = modelo;
        this.idCategoria = idCategoria;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.cor = cor;
        this.tamanho = tamanho;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.ativo = ativo;
        this.caminhoImagem = caminhoImagem; // Inicializa o novo atributo
    }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }
    
    // NOVO: Getters e Setters para o caminho da imagem
    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    } 
    // FIM dos novos métodos

    @Override
    public String toString() {
        return "Produto [idProduto=" + idProduto + ", nome=" + nome + ", marca=" + marca + ", modelo=" + modelo +
               ", idCategoria=" + idCategoria + ", preco=" + preco + ", estoqueMinimo=" + estoqueMinimo + ", ativo=" + ativo +
               ", caminhoImagem=" + caminhoImagem + "]"; // Opcional: Adicionar ao toString
    }
}