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

import br.com.lojaroupa.dao.FuncionarioDAO;
import br.com.lojaroupa.util.EmailService;

@WebServlet("/auth")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

                FuncionarioDAO dao = new FuncionarioDAO();
                Integer idFuncionario = dao.autenticar(email, senha);

                if (idFuncionario != null) {
                    // Gera token de 6 dígitos
                    String codigo2FA = String.format("%06d", new Random().nextInt(999999)); // <--- DESCOMENTADO
                    
                    // Salva temporariamente na sessão
                    HttpSession session = request.getSession(); 
                    session.setAttribute("2fa_codigo", codigo2FA); // <--- DESCOMENTADO
                    session.setAttribute("2fa_user_id", idFuncionario); // <--- DESCOMENTADO
                    
                    // AÇÃO CRUCIAL: FAZ O LOGIN DIRETO
                    //session.setAttribute("usuarioLogado", idFuncionario); // <--- MANTIDO COMENTADO
                    
                    // Envia por E-mail
                    EmailService emailService = new EmailService();
                    emailService.enviarEmail(email, "Seu Código de Acesso", "Seu código é: " + codigo2FA);

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

                if (codigoReal != null && codigoReal.equals(codigoDigitado)) {
                    // Autenticação finalizada com sucesso!
                    session.setAttribute("usuarioLogado", session.getAttribute("2fa_user_id"));
                    session.removeAttribute("2fa_codigo"); // Limpa o token usado
                    
                    jsonRetorno.addProperty("status", "ok");
                } else {
                    jsonRetorno.addProperty("status", "erro");
                    jsonRetorno.addProperty("mensagem", "Código incorreto.");
                }

            // ==========================================
            // AÇÃO 3: LOGOUT (Sair do sistema)
            // ==========================================
            } else if ("logout".equals(acao)) {
                HttpSession session = request.getSession(false); // Pega a sessão se existir
                if (session != null) {
                    session.invalidate(); // Destroi a sessão
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
}