package br.com.lojaroupa.controller;

import java.io.IOException;

// Usamos apenas o pacote 'jakarta' para compatibilidade com seu pom.xml
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    
    // Simplesmente encaminha a requisição para a primeira página de checkout
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verifica se o carrinho está vazio. Se sim, redireciona para o carrinho
        if (request.getSession().getAttribute("carrinho") == null) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }
        
        // Caso contrário, encaminha para a primeira etapa do checkout (endereço)
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
    
    // Se o formulário do checkout.jsp for enviado, o doPost pode ser usado para processar os dados
    // Por enquanto, apenas redirecionamos
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lógica futura: Processar dados de endereço/pagamento e criar pedido.
        // Por enquanto, apenas exibimos uma mensagem simples:
        System.out.println("Dados de checkout recebidos! Próximo passo: Processar pedido.");
        
        // Você pode, por exemplo, encaminhar para uma página de resumo de pedido
        // request.getRequestDispatcher("/resumoPedido.jsp").forward(request, response);
        
        // Ou, por enquanto, apenas voltar à home:
        response.sendRedirect(request.getContextPath() + "/home");
    }
}