package br.com.lojaroupa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	public Connection getConnection() {
		try {
			// Diz ao Java para usar o driver do MySQL que colocamos na pasta lib
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// Conecta ao banco 'lojaroupa_web'
			// IMPORTANTE: Troque 'suasenha' pela senha do seu MySQL
			return DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/lojaroupa?useTimezone=true&serverTimezone=UTC", 
					"root", 
					"");
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException("Erro na conexão com o banco: ", e);
		}
	}
	
	// Método Main apenas para testar se a conexão está funcionando agora
	public static void main(String[] args) {
		try {
			new ConnectionFactory().getConnection();
			System.out.println("✅ Conexão aberta com sucesso!");
		} catch (Exception e) {
			System.out.println("❌ Falha na conexão.");
			e.printStackTrace();
		}
	}
}
