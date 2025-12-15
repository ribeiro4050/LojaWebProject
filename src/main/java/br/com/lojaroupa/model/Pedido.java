package br.com.lojaroupa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; 

public class Pedido {
    
    private int idPedido;
    private int idCliente;
    private Integer idFuncionario; 

    private LocalDateTime dataPedido;
    private String statusPedido;
    private BigDecimal valorTotal;

    // NOVOS CAMPOS ESSENCIAIS PARA O CHECKOUT:
    private String entregaEndereco; 
    private List<ItemCarrinho> itens; 
    
    // NOVOS CAMPOS PARA PREENCHER COLUNAS INDIVIDUAIS DO DB
    private int tipoFrete;
    private String entregaCEP;
    private String entregaTelefone;

    public Pedido() {}

    // Construtor completo (ATUALIZADO com os novos campos)
    public Pedido(int idPedido, int idCliente, Integer idFuncionario, LocalDateTime dataPedido,
                    String statusPedido, BigDecimal valorTotal, String entregaEndereco, List<ItemCarrinho> itens,
                    int tipoFrete, String entregaCEP, String entregaTelefone) {
        
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idFuncionario = idFuncionario;
        this.dataPedido = dataPedido;
        this.statusPedido = statusPedido;
        this.valorTotal = valorTotal;
        this.entregaEndereco = entregaEndereco;
        this.itens = itens;
        this.tipoFrete = tipoFrete;
        this.entregaCEP = entregaCEP;
        this.entregaTelefone = entregaTelefone;
    }

    // --- GETTERS E SETTERS ORIGINAIS ---
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public String getStatusPedido() { return statusPedido; }
    public void setStatusPedido(String statusPedido) { this.statusPedido = statusPedido; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    
    // --- GETTERS E SETTERS DO ENDEREÇO E ITENS ---
    
    public String getEntregaEndereco() { return entregaEndereco; }
    public void setEntregaEndereco(String entregaEndereco) { this.entregaEndereco = entregaEndereco; }

    public List<ItemCarrinho> getItens() { return itens; }
    public void setItens(List<ItemCarrinho> itens) { this.itens = itens; }

    // --- NOVOS GETTERS E SETTERS PARA O DB ---
    
    public int getTipoFrete() { return tipoFrete; }
    public void setTipoFrete(int tipoFrete) { this.tipoFrete = tipoFrete; }

    public String getEntregaCEP() { return entregaCEP; }
    public void setEntregaCEP(String entregaCEP) { this.entregaCEP = entregaCEP; }

    public String getEntregaTelefone() { return entregaTelefone; }
    public void setEntregaTelefone(String entregaTelefone) { this.entregaTelefone = entregaTelefone; }

    @Override
    public String toString() {
        return "Pedido [idPedido=" + idPedido + ", idCliente=" + idCliente + ", idFuncionario=" + idFuncionario +
               ", dataPedido=" + dataPedido + ", statusPedido=" + statusPedido + ", valorTotal=" + valorTotal +
               ", entregaEndereco=" + entregaEndereco + ", entregaCEP=" + entregaCEP + ", entregaTelefone=" + entregaTelefone +
               ", tipoFrete=" + tipoFrete + ", itens.size=" + (itens != null ? itens.size() : 0) + "]";
    }
}