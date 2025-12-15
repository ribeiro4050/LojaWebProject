package br.com.lojaroupa.dao;

import java.sql.Connection;
import java.sql.Date; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import br.com.lojaroupa.model.Cliente;
import br.com.lojaroupa.util.ConnectionFactory; 

public class ClienteDAO {

    // ... (MÉTODO salvarCliente MANTIDO SEM ALTERAÇÕES) ...
    
    /**
     * Salva um novo cliente no banco de dados.
     * @param cliente O objeto Cliente a ser salvo.
     * @return O objeto Cliente atualizado com o idCliente gerado pelo banco.
     */
    public Cliente salvarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes "
                   + "(nome, cpf, email, telefone, endereco, cep, dataCadastro, senha) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, cliente.getNome()); 
            stmt.setString(2, cliente.getCpf()); 
            stmt.setString(3, cliente.getEmail()); 
            stmt.setString(4, cliente.getTelefone()); 
            stmt.setString(5, cliente.getEndereco()); 
            stmt.setString(6, cliente.getCep()); 
            stmt.setDate(7, Date.valueOf(LocalDate.now())); 
            stmt.setString(8, cliente.getSenha()); 
            
            stmt.executeUpdate();
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cliente.setIdCliente(rs.getInt(1)); 
            } else {
                 throw new SQLException("Falha ao obter ID do cliente. Nenhuma chave gerada.");
            }

            return cliente;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o cliente no banco: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { }
        }
    }

    // ... (MÉTODO existeCliente MANTIDO SEM ALTERAÇÕES) ...

    /**
     * Verifica se um cliente já existe no banco (pelo CPF ou E-mail).
     * @param campo O nome da coluna ('cpf' ou 'email').
     * @param valor O valor a ser pesquisado.
     * @return true se o cliente existir, false caso contrário.
     */
    public boolean existeCliente(String campo, String valor) {
        if (!campo.equalsIgnoreCase("cpf") && !campo.equalsIgnoreCase("email")) {
            throw new IllegalArgumentException("Campo de pesquisa inválido. Use 'cpf' ou 'email'.");
        }
        
        String sql = "SELECT COUNT(*) FROM clientes WHERE " + campo + " = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, valor);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar a existência do cliente por " + campo + ": " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { }
        }
    }
    
    // ==========================================================
    // MÉTODO NOVO: PARA LOGIN
    // ==========================================================
    
    /**
     * Busca e valida um cliente com base no email e senha.
     * @param email O email do cliente.
     * @param senha A senha do cliente.
     * @return O objeto Cliente se as credenciais estiverem corretas, ou null caso contrário.
     */
    public Cliente buscarClientePorEmailESenha(String email, String senha) {
        // Busca na tabela por email e senha
        String sql = "SELECT * FROM clientes WHERE email = ? AND senha = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cliente cliente = null;

        try {
            conn = new ConnectionFactory().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, senha); // ATENÇÃO: Se usar Hash no futuro, esta linha deve mudar.
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Mapeia os dados do ResultSet para o objeto Cliente
                cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setEndereco(rs.getString("endereco"));
                cliente.setCep(rs.getString("cep"));
                
                // Converte java.sql.Date para java.time.LocalDate
                Date dataSql = rs.getDate("dataCadastro");
                if (dataSql != null) {
                    cliente.setDataCadastro(dataSql.toLocalDate());
                }
                
                cliente.setSenha(rs.getString("senha")); 
            }
            
            return cliente; // Retorna o cliente ou null

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente para login: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { }
        }
    }
}