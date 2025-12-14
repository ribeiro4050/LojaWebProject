package br.com.lojaroupa.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Importar as classes DAO e Model necessárias
import br.com.lojaroupa.dao.ProdutoDAO;
import br.com.lojaroupa.model.Produto;

@WebServlet("/home")
public class HomeProdutosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public HomeProdutosServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			// 1. Instancia o DAO (a camada de acesso ao banco)
			ProdutoDAO dao = new ProdutoDAO();
			
			// 2. Busca a lista de produtos (usando o método listarTodos() do DAO)
			List<Produto> listaProdutos = dao.listarTodos();
			
			// 3. Coloca a lista na Requisição (para ser acessada pelo JSP)
			// A variável no JSP será chamada ${listaProdutos}
			request.setAttribute("listaProdutos", listaProdutos);
			
			// 4. Encaminha (forward) para a página JSP (a Vitrine/View)
			RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
			dispatcher.forward(request, response);

		} catch (Exception e) {
			System.err.println("❌ Erro ao carregar a página home: " + e.getMessage());
			e.printStackTrace();
			// Retorna um erro HTTP 500 para o navegador em caso de falha no servidor
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Não foi possível carregar os produtos.");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Geralmente o doGet é suficiente para exibir uma lista
		doGet(request, response);
	}
}