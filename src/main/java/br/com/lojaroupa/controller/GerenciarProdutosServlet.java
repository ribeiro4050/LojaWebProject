package br.com.lojaroupa.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/gerenciar-produtos")
public class GerenciarProdutosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
		
        // 1. Verifica se o funcionário está logado (Guarda de Segurança)
        if (request.getSession().getAttribute("funcionarioLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/home"); // Se não estiver logado, redireciona
            return;
        }
        
		// 2. Encaminha para a página de gerenciamento (index.jsp)
        // OBS: Você pode querer criar uma página específica como /gerenciar_produtos.jsp
        // Mas como você pediu para usar o index.jsp, faremos isso por enquanto.
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
}