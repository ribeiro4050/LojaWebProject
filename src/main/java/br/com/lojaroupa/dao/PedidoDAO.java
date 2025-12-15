package br.com.lojaroupa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import br.com.lojaroupa.model.ItemCarrinho;
import br.com.lojaroupa.model.Pedido;
import br.com.lojaroupa.util.ConnectionFactory; // Mantendo o seu import original

/**
 * Classe responsável por gerenciar a persistência de objetos Pedido e seus
 * itens no banco de dados, utilizando transações.
 */
public class PedidoDAO {

    public PedidoDAO() {
        // Construtor vazio
    }

    /**
     * Salva o pedido (cabeçalho) e todos os seus itens (detalhes) em uma única transação.
     * @param pedido O objeto Pedido preenchido (cliente, endereço, valor total e itens).
     * @return O objeto Pedido atualizado com o ID gerado pelo MySQL.
     */
    public Pedido salvarPedido(Pedido pedido) {
        
        Connection conn = null;
        PreparedStatement stmtPedido = null;
        PreparedStatement stmtItem = null;
        ResultSet rs = null;
        
        // SQL ATUALIZADO: Adicionando tipoFrete, entregaCEP e entregaTelefone
        String sqlPedido = "INSERT INTO pedidos (idCliente, idFuncionario, dataPedido, valorTotal, entregaEndereco, statusPedido, tipoFrete, entregaCEP, entregaTelefone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // SQL para inserir os detalhes (mantido o nome original da coluna que você usou)
        String sqlItem = "INSERT INTO detalhes_pedido_venda (idPedido, idProduto, quantidade, preco_unitario_na_compra) VALUES (?, ?, ?, ?)";

        try {
            conn = new ConnectionFactory().getConnection();
            
            // 1. Inicia a transação: Se uma parte falhar, tudo é desfeito (rollback).
            conn.setAutoCommit(false); 

            // 2. Insere o Cabeçalho do Pedido (Tabela 'pedidos')
            // Statement.RETURN_GENERATED_KEYS é crucial para obter o ID gerado automaticamente.
            stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            
            // PARÂMETROS EXISTENTES (1 a 3)
            // 1. idCliente
            stmtPedido.setInt(1, pedido.getIdCliente()); 
            
            // 2. idFuncionario (Pode ser nulo para compra web)
            if (pedido.getIdFuncionario() != null) {
                stmtPedido.setInt(2, pedido.getIdFuncionario());
            } else {
                stmtPedido.setNull(2, java.sql.Types.INTEGER); // Seta NULL para FK que aceita null
            }
            
            // 3. dataPedido
            stmtPedido.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            // PARÂMETROS EXISTENTES (4 a 6)
            // 4. valorTotal
            stmtPedido.setBigDecimal(4, pedido.getValorTotal()); 
            // 5. entregaEndereco
            stmtPedido.setString(5, pedido.getEntregaEndereco()); 
            // 6. statusPedido
            stmtPedido.setString(6, "NOVO"); 
            
            // NOVOS PARÂMETROS (7 a 9)
            // 7. tipoFrete
            stmtPedido.setInt(7, pedido.getTipoFrete()); 
            // 8. entregaCEP
            stmtPedido.setString(8, pedido.getEntregaCEP()); 
            // 9. entregaTelefone
            stmtPedido.setString(9, pedido.getEntregaTelefone()); 
            
            stmtPedido.executeUpdate();
            
            // 3. Recupera o ID gerado pelo MySQL
            rs = stmtPedido.getGeneratedKeys();
            int idPedidoGerado = 0;
            if (rs.next()) {
                idPedidoGerado = rs.getInt(1);
                pedido.setIdPedido(idPedidoGerado); // <--- ATUALIZA O OBJETO PEDIDO COM O ID REAL
            } else {
                 throw new SQLException("Falha ao obter ID do pedido. Nenhuma chave gerada.");
            }

            // 4. Insere os Itens do Pedido (Tabela 'detalhes_pedido_venda')
            stmtItem = conn.prepareStatement(sqlItem);
            
            for (ItemCarrinho item : pedido.getItens()) {
                // É CRUCIAL que item.getProduto() não seja nulo e tenha o ID.
                if (item.getProduto() == null || item.getProduto().getIdProduto() == 0) {
                     throw new RuntimeException("Item do carrinho sem dados de Produto válidos.");
                }
                
                stmtItem.setInt(1, idPedidoGerado); // Liga ao pedido que acabamos de inserir
                stmtItem.setInt(2, item.getProduto().getIdProduto());
                stmtItem.setInt(3, item.getQuantidade()); 
                stmtItem.setBigDecimal(4, item.getProduto().getPreco()); 
                
                stmtItem.addBatch(); // Prepara o comando
            }
            
            stmtItem.executeBatch(); // Executa o lote de inserções

            // 5. Finaliza a transação
            conn.commit(); 
            
            return pedido;

        } catch (SQLException e) {
            // Em caso de erro, desfazemos as alterações
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                // ...
            }
            throw new RuntimeException("Erro ao salvar o pedido no banco: " + e.getMessage(), e);
        } finally {
            // Garante que todos os recursos sejam fechados, mesmo em caso de erro.
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (rs != null) rs.close();
                if (stmtPedido != null) stmtPedido.close();
                if (stmtItem != null) stmtItem.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // ...
            }
        }
    }
}