package br.com.lojaroupa.controller;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

// Importações atualizadas para usar ClienteDAO e Cliente
import br.com.lojaroupa.dao.ClienteDAO; 
import br.com.lojaroupa.model.Cliente; 
import br.com.lojaroupa.util.EmailService;

@WebServlet("/auth")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Método que lida com o login AJAX (POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurações padrão de resposta JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String acao = request.getParameter("acao");
        Gson gson = new Gson();
        JsonObject jsonRetorno = new JsonObject();

        try {
            // ==========================================
            // AÇÃO 1: LOGIN (Valida Senha + Envia E-mail)
            // ==========================================
            if ("login".equals(acao)) {
                String email = request.getParameter("email");
                String senha = request.getParameter("senha");

                // *** MUDANÇA: USANDO CLIENTE DAO ***
                ClienteDAO dao = new ClienteDAO(); 
                Cliente clienteLogado = dao.buscarClientePorEmailESenha(email, senha); // Usa o método que criamos

                if (clienteLogado != null) {
                    // Gera token de 6 dígitos
                    String codigo2FA = String.format("%06d", new Random().nextInt(999999));
                    
                    // Salva temporariamente na sessão
                    HttpSession session = request.getSession(); 
                    session.setAttribute("2fa_codigo", codigo2FA); 
                    
                    // *** MUDANÇA: SALVA O OBJETO CLIENTE TEMPORARIAMENTE ***
                    session.setAttribute("2fa_cliente", clienteLogado); 
                    
                    // Envia por E-mail (o EmailService precisa estar configurado, ex: para Mailtrap)
                    EmailService emailService = new EmailService();
                    emailService.enviarEmail(email, "Seu Código de Acesso", "Seu código é: " + codigo2FA);

                    // Imprime no console para facilitar o teste local
                    System.out.println("--- CÓDIGO 2FA ENVIADO para " + email + ": " + codigo2FA + " ---");
                    
                    jsonRetorno.addProperty("status", "ok");
                } else {
                    jsonRetorno.addProperty("status", "erro");
                    jsonRetorno.addProperty("mensagem", "E-mail ou senha inválidos.");
                }

            // ==========================================
            // AÇÃO 2: VALIDAR 2FA (Confere o código)
            // ==========================================
            } else if ("validar2fa".equals(acao)) {
                String codigoDigitado = request.getParameter("codigo");
                HttpSession session = request.getSession();
                
                String codigoReal = (String) session.getAttribute("2fa_codigo");
                // *** MUDANÇA: RECUPERA O OBJETO CLIENTE ***
                Cliente clienteParaLogar = (Cliente) session.getAttribute("2fa_cliente");

                if (codigoReal != null && clienteParaLogar != null && codigoReal.equals(codigoDigitado)) {
                    // Autenticação finalizada com sucesso!
                    
                    // *** MUDANÇA: SALVA O OBJETO CLIENTE FINALMENTE LOGADO ***
                    session.setAttribute("clienteLogado", clienteParaLogar); 
                    
                    // Limpa os atributos temporários de 2FA
                    session.removeAttribute("2fa_codigo"); 
                    session.removeAttribute("2fa_cliente"); 
                    
                    jsonRetorno.addProperty("status", "ok");
                } else {
                    jsonRetorno.addProperty("status", "erro");
                    jsonRetorno.addProperty("mensagem", "Código incorreto.");
                }

            // ==========================================
            // AÇÃO 3: LOGOUT (Sair do sistema)
            // ==========================================
            } else if ("logout".equals(acao)) {
                HttpSession session = request.getSession(false); 
                if (session != null) {
                    session.invalidate(); 
                }
                jsonRetorno.addProperty("status", "ok");
            }

        } catch (Exception e) {
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Erro interno: " + e.getMessage());
            e.printStackTrace();
        }

        // Devolve o JSON para o JavaScript
        response.getWriter().write(gson.toJson(jsonRetorno));
    }
    
    // Método que lida com o acesso direto via URL (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}