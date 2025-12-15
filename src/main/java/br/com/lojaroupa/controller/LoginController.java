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

// Importações dos DAOs e Models
import br.com.lojaroupa.dao.ClienteDAO; 
import br.com.lojaroupa.model.Cliente; 
import br.com.lojaroupa.dao.FuncionarioDAO; // NOVO: DAO do Funcionário
import br.com.lojaroupa.model.Funcionario; // NOVO: Model do Funcionário

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
                
                // VARIÁVEIS DE AUTENTICAÇÃO UNIFICADAS
                Object usuarioAutenticado = null;
                String tipoUsuario = null; // "cliente" ou "funcionario"

                // 1. TENTATIVA COMO CLIENTE
                ClienteDAO clienteDAO = new ClienteDAO();
                Cliente cliente = clienteDAO.buscarClientePorEmailESenha(email, senha);

                if (cliente != null) {
                    usuarioAutenticado = cliente;
                    tipoUsuario = "cliente";
                } else {
                    // 2. SE NÃO FOR CLIENTE, TENTA COMO FUNCIONÁRIO
                    FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
                    // Assumimos que o método buscarPorEmailESenha() foi adicionado ao FuncionarioDAO
                    Funcionario funcionario = funcionarioDAO.buscarPorEmailESenha(email, senha);

                    if (funcionario != null) {
                        usuarioAutenticado = funcionario;
                        tipoUsuario = "funcionario";
                    }
                }

                // 3. VERIFICA SE ALGUÉM FOI AUTENTICADO
                if (usuarioAutenticado != null) {
                    // Gera token de 6 dígitos
                    String codigo2FA = String.format("%06d", new Random().nextInt(999999));
                    
                    HttpSession session = request.getSession(); 
                    session.setAttribute("2fa_codigo", codigo2FA); 
                    session.setAttribute("2fa_usuario", usuarioAutenticado); // Salva Cliente OU Funcionario
                    session.setAttribute("2fa_tipo", tipoUsuario);         // Salva o tipo (para uso futuro)

                    // Envia por E-mail (usando o email fornecido)
                    EmailService emailService = new EmailService();
                    emailService.enviarEmail(email, "Seu Código de Acesso", "Seu código é: " + codigo2FA);

                    System.out.println("--- CÓDIGO 2FA ENVIADO para " + email + " (" + tipoUsuario.toUpperCase() + "): " + codigo2FA + " ---");
                    
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
                Object usuarioParaLogar = session.getAttribute("2fa_usuario"); // Cliente ou Funcionario
                String tipoUsuario = (String) session.getAttribute("2fa_tipo"); // Tipo

                if (codigoReal != null && usuarioParaLogar != null && codigoReal.equals(codigoDigitado)) {
                    // Autenticação finalizada com sucesso!
                    
                    // Salva na sessão com a chave específica (clienteLogado ou funcionarioLogado)
                    if ("funcionario".equals(tipoUsuario)) {
                         session.setAttribute("funcionarioLogado", usuarioParaLogar); 
                    } else {
                         session.setAttribute("clienteLogado", usuarioParaLogar); 
                    }
                    
                    // Limpa os atributos temporários de 2FA
                    session.removeAttribute("2fa_codigo"); 
                    session.removeAttribute("2fa_usuario"); 
                    session.removeAttribute("2fa_tipo");
                    
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
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}