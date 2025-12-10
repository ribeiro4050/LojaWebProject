package br.com.lojaroupa.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import br.com.lojaroupa.dao.ProdutoDAO;
import br.com.lojaroupa.model.Produto;

// Esse endereço "/produtos" é o que o AJAX vai chamar
@WebServlet("/produtos")
public class ProdutoController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// O método doGet é chamado quando o navegador pede dados (Listar)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 1. Chama o DAO para buscar a lista no banco
		ProdutoDAO dao = new ProdutoDAO();
		List<Produto> listaProdutos = dao.listarTodos();
		
		// 2. Converte a lista de Java para JSON (Texto)
		Gson gson = new Gson();
		String json = gson.toJson(listaProdutos);
		
		// 3. Configura a resposta para o navegador entender que é JSON e aceitar acentos
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		// 4. Envia a resposta
		response.getWriter().write(json);
	}
}