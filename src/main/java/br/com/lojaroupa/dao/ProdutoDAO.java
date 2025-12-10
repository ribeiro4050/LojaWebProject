package br.com.lojaroupa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.lojaroupa.model.Produto;
import br.com.lojaroupa.util.ConnectionFactory;

public class ProdutoDAO {

    // 1. INSERIR (CREATE)
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome, marca, modelo, idCategoria, descricao, unidadeMedida, cor, tamanho, preco, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // AQUI ESTÁ A MÁGICA: Usamos a fábrica nova
            conn = new ConnectionFactory().getConnection();
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getMarca());
            stmt.setString(3, produto.getModelo());
            stmt.setInt(4, produto.getIdCategoria());
            stmt.setString(5, produto.getDescricao());
            stmt.setString(6, produto.getUnidadeMedida());
            stmt.setString(7, produto.getCor());
            stmt.setString(8, produto.getTamanho());
            stmt.setBigDecimal(9, produto.getPreco());
            stmt.setString(10, produto.getAtivo());

            stmt.executeUpdate();
            System.out.println("✅ Produto '" + produto.getNome() + "' inserido com sucesso!");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir produto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // 2. LISTAR TODOS (READ)
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("idProduto"));
                produto.setNome(rs.getString("nome"));
                produto.setMarca(rs.getString("marca"));
                produto.setModelo(rs.getString("modelo"));
                produto.setIdCategoria(rs.getInt("idCategoria"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setUnidadeMedida(rs.getString("unidadeMedida"));
                produto.setCor(rs.getString("cor"));
                produto.setTamanho(rs.getString("tamanho"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setAtivo(rs.getString("ativo"));
                
                produtos.add(produto);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return produtos;
    }

    // 3. BUSCAR POR ID (READ SINGLE)
    public Produto buscarPorId(int id) {
        Produto produto = null;
        String sql = "SELECT * FROM produtos WHERE idProduto = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                produto = new Produto();
                produto.setIdProduto(rs.getInt("idProduto"));
                produto.setNome(rs.getString("nome"));
                produto.setMarca(rs.getString("marca"));
                produto.setModelo(rs.getString("modelo"));
                produto.setIdCategoria(rs.getInt("idCategoria"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setUnidadeMedida(rs.getString("unidadeMedida"));
                produto.setCor(rs.getString("cor"));
                produto.setTamanho(rs.getString("tamanho"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setAtivo(rs.getString("ativo"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return produto;
    }

    // 4. ATUALIZAR (UPDATE)
    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, marca = ?, modelo = ?, idCategoria = ?, descricao = ?, unidadeMedida = ?, cor = ?, tamanho = ?, preco = ?, ativo = ? WHERE idProduto = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getMarca());
            stmt.setString(3, produto.getModelo());
            stmt.setInt(4, produto.getIdCategoria());
            stmt.setString(5, produto.getDescricao());
            stmt.setString(6, produto.getUnidadeMedida());
            stmt.setString(7, produto.getCor());
            stmt.setString(8, produto.getTamanho());
            stmt.setBigDecimal(9, produto.getPreco());
            stmt.setString(10, produto.getAtivo());
            stmt.setInt(11, produto.getIdProduto());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Produto ID " + produto.getIdProduto() + " atualizado!");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // 5. DELETAR (DELETE)
    public void deletar(int id) {
        String sql = "DELETE FROM produtos WHERE idProduto = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Produto ID " + id + " deletado!");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao deletar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // Método auxiliar para fechar conexões e não repetir código (Clean Code)
    private void fecharConexao(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    // ==========================================
    // MÉTODO MAIN PARA TESTE RÁPIDO NO CONSOLE
    // ==========================================
    public static void main(String[] args) {
        ProdutoDAO dao = new ProdutoDAO();
        
        // Teste de Listagem
        System.out.println("📦 Listando produtos do banco...");
        List<Produto> lista = dao.listarTodos();
        for (Produto p : lista) {
            System.out.println(p.getIdProduto() + " - " + p.getNome() + " | R$ " + p.getPreco());
        }
    }
}