package br.com.lojaroupa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.lojaroupa.model.Funcionario;
import br.com.lojaroupa.util.ConnectionFactory;

public class FuncionarioDAO {

    //  MÉTODO ESPECIAL PARA O LOGIN (Adicionei este)
    public Integer autenticar(String email, String senha) {
        String sql = "SELECT idFuncionario FROM funcionarios WHERE email = ? AND senha = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("idFuncionario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return null;
    }

    // SEU MÉTODO ANTIGO ADAPTADO (Inserir)
    public void inserir(Funcionario funcionario) {
        // Ajustei os nomes das colunas para bater com seu banco atual
        String sql = "INSERT INTO funcionarios (email, senha, idNivelUsuario, nome, cpf, endereco, cep, telefone, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = new ConnectionFactory().getConnection(); // <--- Mudança aqui
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, funcionario.getEmail());
            stmt.setString(2, funcionario.getSenha());
            stmt.setInt(3, funcionario.getIdNivelUsuario());
            stmt.setString(4, funcionario.getNome());
            stmt.setString(5, funcionario.getCpf());
            stmt.setString(6, funcionario.getEndereco());
            stmt.setString(7, funcionario.getCep());
            stmt.setString(8, funcionario.getTelefone());
            stmt.setString(9, funcionario.getAtivo());

            stmt.executeUpdate();
            System.out.println("✅ Funcionário inserido com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir: " + e.getMessage());
        } finally {
            fecharConexao(conn, stmt, null);
        }
    }

    // LISTAR TODOS ADAPTADO
    public List<Funcionario> listarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        String sql = "SELECT * FROM funcionarios ORDER BY nome";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setIdFuncionario(rs.getInt("idFuncionario"));
                f.setEmail(rs.getString("email"));
                f.setSenha(rs.getString("senha"));
                f.setIdNivelUsuario(rs.getInt("idNivelUsuario"));
                f.setNome(rs.getString("nome"));
                f.setCpf(rs.getString("cpf"));
                f.setEndereco(rs.getString("endereco"));
                f.setCep(rs.getString("cep"));
                f.setTelefone(rs.getString("telefone"));
                f.setAtivo(rs.getString("ativo"));
                funcionarios.add(f);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fecharConexao(conn, stmt, rs);
        }
        return funcionarios;
    }
    
    // Método auxiliar para fechar tudo limpo
    private void fecharConexao(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}