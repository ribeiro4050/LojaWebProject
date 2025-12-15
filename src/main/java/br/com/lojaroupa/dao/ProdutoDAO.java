package br.com.lojaroupa.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.lojaroupa.model.Produto;
import br.com.lojaroupa.util.ConnectionFactory;

public class ProdutoDAO {

    private void fecharConexao(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===============================================
    // 1. INSERIR (CREATE) - PRODUTOS E ESTOQUE
    // ===============================================
    public void inserir(Produto produto) {
        String sqlProdutos = "INSERT INTO produtos (nome, marca, modelo, tamanho, preco, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlEstoque = "INSERT INTO estoque (idProduto, dtEntrada, quantidade) VALUES (?, CURDATE(), ?)";
        
        Connection conn = null;
        PreparedStatement stmtProdutos = null;
        PreparedStatement stmtEstoque = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false); 

            // A. Insere na tabela 'produtos'
            stmtProdutos = conn.prepareStatement(sqlProdutos, Statement.RETURN_GENERATED_KEYS);
            
            stmtProdutos.setString(1, produto.getNome());
            stmtProdutos.setString(2, produto.getMarca());
            stmtProdutos.setString(3, produto.getModelo());
            stmtProdutos.setString(4, produto.getTamanho());
            stmtProdutos.setBigDecimal(5, produto.getPreco());
            stmtProdutos.setString(6, produto.getAtivo());  

            stmtProdutos.executeUpdate();

            rs = stmtProdutos.getGeneratedKeys();
            int idProdutoGerado = -1;
            if (rs.next()) {
                idProdutoGerado = rs.getInt(1);
            }
            
            // B. Insere a quantidade inicial na tabela 'estoque'
            if (idProdutoGerado != -1) {
                stmtEstoque = conn.prepareStatement(sqlEstoque);
                stmtEstoque.setInt(1, idProdutoGerado);
                stmtEstoque.setInt(2, produto.getEstoqueMinimo()); 
                stmtEstoque.executeUpdate();
            }
            
            conn.commit(); 
            System.out.println("✅ Produto inserido com sucesso: " + produto.getNome() + " e estoque inicial registrado.");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackE) {
                rollbackE.printStackTrace();
            }
            System.err.println("❌ Erro ao inserir o produto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(null, stmtEstoque, null);
            fecharConexao(conn, stmtProdutos, rs);
        }
    }

    // ===============================================
    // 2. LISTAR TODOS (READ)
    // ===============================================
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        
        String sql = "SELECT p.*, " +
                     "  (IFNULL(SUM(e.quantidade), 0) - IFNULL(SUM(e.qtdVendida), 0)) AS estoque_atual " +
                     "FROM produtos p " +
                     "LEFT JOIN estoque e ON p.idProduto = e.idProduto " +
                     "GROUP BY p.idProduto " +
                     "ORDER BY p.idProduto DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("idProduto"));
                p.setNome(rs.getString("nome"));
                p.setMarca(rs.getString("marca"));
                p.setModelo(rs.getString("modelo"));
                p.setTamanho(rs.getString("tamanho"));
                p.setPreco(rs.getBigDecimal("preco")); 
                p.setAtivo(rs.getString("ativo"));
                p.setEstoqueMinimo(rs.getInt("estoque_atual")); // Mapeando estoque_atual
                
                produtos.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return produtos;
    }
    
    
    // ===============================================
    // NOVO MÉTODO: CONTAGEM TOTAL (PARA PAGINAÇÃO)
    // ===============================================

    /**
     * Retorna o número total de produtos na tabela 'produtos', opcionalmente filtrado.
     * @param filtro Termo de busca opcional.
     */
    public int contarTotalProdutos(String filtro) {
        // Query que conta produtos, usando LEFT JOIN para garantir que o filtro funciona
        // mesmo se a condição incluir estoque no futuro.
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT p.idProduto) FROM produtos p ");
        sql.append("LEFT JOIN estoque e ON p.idProduto = e.idProduto ");
        sql.append("WHERE 1=1 "); 
        
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (p.nome LIKE ? OR p.marca LIKE ? OR p.modelo LIKE ?) ");
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int total = 0;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql.toString());
            
            int index = 1;
            if (filtro != null && !filtro.trim().isEmpty()) {
                String termo = "%" + filtro + "%";
                stmt.setString(index++, termo);
                stmt.setString(index++, termo);
                stmt.setString(index++, termo);
            }
            
            rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar o total de produtos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return total;
    }


    // ===============================================
    // NOVO MÉTODO: LISTAR COM PAGINAÇÃO E FILTRO (READ)
    // ===============================================

    /**
     * Lista produtos com paginação, filtro opcional e calcula o estoque.
     * @param limite Número máximo de registros por página.
     * @param offset Ponto de partida (página * limite).
     * @param filtro Termo de busca opcional.
     * @return Lista de produtos da página solicitada.
     */
    public List<Produto> listarComPaginacao(int limite, int offset, String filtro) {
        List<Produto> produtos = new ArrayList<>();
        
        // Base da Query com cálculo de estoque
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, ");
        sql.append("  (IFNULL(SUM(e.quantidade), 0) - IFNULL(SUM(e.qtdVendida), 0)) AS estoque_atual ");
        sql.append("FROM produtos p ");
        sql.append("LEFT JOIN estoque e ON p.idProduto = e.idProduto ");
        sql.append("WHERE 1=1 "); // Ponto de ancoragem para o filtro
        
        // Adiciona o filtro se ele existir
        if (filtro != null && !filtro.trim().isEmpty()) {
            sql.append("AND (p.nome LIKE ? OR p.marca LIKE ? OR p.modelo LIKE ?) ");
        }
        
        sql.append("GROUP BY p.idProduto ");
        sql.append("ORDER BY p.idProduto DESC "); // Ordem
        sql.append("LIMIT ? OFFSET ?"); // Paginação

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql.toString());
            
            int index = 1;
            
            // 1. Aplicação do filtro (se houver)
            if (filtro != null && !filtro.trim().isEmpty()) {
                String termo = "%" + filtro + "%";
                stmt.setString(index++, termo);
                stmt.setString(index++, termo);
                stmt.setString(index++, termo);
            }
            
            // 2. Aplicação da paginação
            stmt.setInt(index++, limite);
            stmt.setInt(index++, offset);
            
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("idProduto"));
                p.setNome(rs.getString("nome"));
                p.setMarca(rs.getString("marca"));
                p.setModelo(rs.getString("modelo"));
                p.setTamanho(rs.getString("tamanho"));
                p.setPreco(rs.getBigDecimal("preco")); 
                p.setAtivo(rs.getString("ativo"));
                // O valor do estoque_atual é mapeado para o atributo estoqueMinimo
                p.setEstoqueMinimo(rs.getInt("estoque_atual")); 
                
                produtos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos com paginação/filtro: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return produtos;
    }


    // ===============================================
    // 3. BUSCAR POR ID (READ SINGLE) 
    // ===============================================
    public Produto buscarPorId(int idProduto) {
        Produto produto = null;
        
        String sql = "SELECT p.*, " +
                     "  (IFNULL(SUM(e.quantidade), 0) - IFNULL(SUM(e.qtdVendida), 0)) AS estoque_atual " +
                     "FROM produtos p " +
                     "LEFT JOIN estoque e ON p.idProduto = e.idProduto " +
                     "WHERE p.idProduto = ? " +
                     "GROUP BY p.idProduto"; 
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idProduto); 
            rs = stmt.executeQuery();

            if (rs.next()) {
                produto = new Produto();
                produto.setIdProduto(rs.getInt("idProduto"));
                produto.setNome(rs.getString("nome"));
                produto.setMarca(rs.getString("marca"));
                produto.setModelo(rs.getString("modelo"));
                produto.setTamanho(rs.getString("tamanho"));
                produto.setPreco(rs.getBigDecimal("preco")); 
                produto.setAtivo(rs.getString("ativo"));
                produto.setEstoqueMinimo(rs.getInt("estoque_atual")); 
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return produto;
    }


    // ===============================================
    // 4. ATUALIZAR (UPDATE)
    // ===============================================
    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, marca = ?, modelo = ?, tamanho = ?, preco = ?, ativo = ? WHERE idProduto = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getMarca());
            stmt.setString(3, produto.getModelo());
            stmt.setString(4, produto.getTamanho());
            stmt.setBigDecimal(5, produto.getPreco());
            stmt.setString(6, produto.getAtivo());
            stmt.setInt(7, produto.getIdProduto()); 
            
            stmt.executeUpdate();
            System.out.println("✅ Produto ID " + produto.getIdProduto() + " atualizado na tabela produtos.");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }
    
    // ===============================================
    // 5. DELETAR (DELETE) - COM TRANSAÇÃO 
    // ===============================================
    public void deletar(int id) {
        String sqlDeleteEstoque = "DELETE FROM estoque WHERE idProduto = ?";
        String sqlDeleteProdutos = "DELETE FROM produtos WHERE idProduto = ?";
        
        Connection conn = null;
        PreparedStatement stmtEstoque = null;
        PreparedStatement stmtProdutos = null;

        try {
            conn = new ConnectionFactory().getConnection();
            conn.setAutoCommit(false);
            
            // 1. Deleta da tabela 'estoque'
            stmtEstoque = conn.prepareStatement(sqlDeleteEstoque);
            stmtEstoque.setInt(1, id);
            stmtEstoque.executeUpdate();

            // 2. Deleta da tabela 'produtos'
            stmtProdutos = conn.prepareStatement(sqlDeleteProdutos);
            stmtProdutos.setInt(1, id);
            int linhasAfetadas = stmtProdutos.executeUpdate();

            if (linhasAfetadas > 0) {
                conn.commit();
                System.out.println("✅ Produto ID " + id + " deletado (Produto e Estoque).");
            } else {
                conn.rollback();
                System.out.println("⚠️ Produto ID " + id + " não encontrado para deleção.");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackE) {
                rollbackE.printStackTrace();
            }
            System.err.println("Erro ao deletar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(null, stmtEstoque, null);
            fecharConexao(conn, stmtProdutos, null);
        }
    }
    
}