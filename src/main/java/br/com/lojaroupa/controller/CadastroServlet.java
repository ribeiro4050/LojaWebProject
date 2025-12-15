package br.com.lojaroupa.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.lojaroupa.dao.ClienteDAO;
import br.com.lojaroupa.model.Cliente;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // ===============================================
    // GET: USADO PARA VALIDAÇÃO AJAX EM TEMPO REAL
    // ===============================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Parâmetros que virão da requisição AJAX:
        String campo = request.getParameter("campo"); // 'cpf' ou 'email'
        String valor = request.getParameter("valor"); // O valor digitado

        // Se a requisição veio com parâmetros de validação
        if (campo != null && valor != null) {
            
            // Configura a resposta como JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            
            ClienteDAO clienteDAO = new ClienteDAO();
            boolean existe = false;

            try {
                // 1. Limpa o valor (especialmente o CPF) antes de verificar no BD
                String valorLimpo = valor.replaceAll("[^0-9a-zA-Z@.]", "");
                
                // 2. Chama o DAO para verificar a existência
                // O método existeCliente já lida com 'cpf' ou 'email'
                if (!valorLimpo.isEmpty()) {
                    existe = clienteDAO.existeCliente(campo, valorLimpo);
                }

                // 3. Retorna o JSON de resposta
                // Retorna 'true' se o valor JÁ EXISTE no BD (indicando erro)
                out.print("{\"existe\": " + existe + "}"); 

            } catch (Exception e) {
                // Em caso de erro interno, melhor retornar que não existe (ou um erro genérico)
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"existe\": true, \"erro\": \"Erro interno no servidor.\"}"); 
                e.printStackTrace();
            } finally {
                out.flush();
            }
            return; // Interrompe o método GET aqui.
        }
        
        // Se o GET não tiver parâmetros de validação, apenas exibe o formulário (padrão)
        request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
    }
    
    // ===============================================
    // POST: USADO PARA SUBMISSÃO E SALVAMENTO FINAL
    // ===============================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Receber os parâmetros do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");
        String telefone = request.getParameter("telefone");
        String cpf = request.getParameter("cpf");
        
        // Inicializa o DAO para uso
        ClienteDAO clienteDAO = new ClienteDAO();
        
        // Mensagem de erro padrão
        String erro = null;
        
        // 2. Validação Básica dos Dados
        if (nome == null || nome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            senha == null || senha.trim().isEmpty() ||
            confirmarSenha == null || confirmarSenha.trim().isEmpty() ||
            telefone == null || telefone.trim().isEmpty() ||
            cpf == null || cpf.trim().isEmpty()) {
            
            erro = "Todos os campos (Nome, E-mail, Senha, Telefone, CPF) são obrigatórios.";
            
        } else if (!senha.equals(confirmarSenha)) {
            erro = "A senha e a confirmação de senha não coincidem.";
            
        } else if (senha.length() < 6) {
            erro = "A senha deve ter no mínimo 6 caracteres.";
        }
        
        // 3. Validação de Duplicidade (Revalidação no Servidor)
        if (erro == null) {
            try {
                // Remove formatação para pesquisa no BD
                String cpfLimpo = cpf.replaceAll("[^0-9]", ""); 

                // ATENÇÃO: Essa lógica de validação é crítica. Mesmo com AJAX no front, 
                // SEMPRE devemos revalidar no POST.
                if (clienteDAO.existeCliente("cpf", cpfLimpo)) {
                    erro = "Este CPF já está cadastrado no sistema.";
                } else if (clienteDAO.existeCliente("email", email)) {
                    erro = "Este E-mail já está cadastrado no sistema.";
                }
                
                // Atualiza o CPF com a versão limpa
                cpf = cpfLimpo; 

            } catch (RuntimeException e) {
                e.printStackTrace();
                erro = "Erro interno ao validar dados. Tente novamente mais tarde.";
            }
        }
        
        // 4. Se houver erro, retorna ao formulário
        if (erro != null) {
            request.setAttribute("erro", erro);
            // Preserva os dados digitados (exceto a senha)
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.setAttribute("telefone", telefone);
            request.setAttribute("cpf", cpf);
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
            return;
        }
        
        // 5. Salvar o Cliente no Banco de Dados (Se não houver erros)
        try {
            Cliente novoCliente = new Cliente();
            novoCliente.setNome(nome);
            novoCliente.setEmail(email);
            novoCliente.setCpf(cpf);
            novoCliente.setTelefone(telefone);
            novoCliente.setSenha(senha); // Lembre-se: Usar Hash em produção!
            novoCliente.setDataCadastro(LocalDate.now()); 
            
            // Campos que não vieram do formulário de cadastro, mas são necessários no modelo:
            // Você deve garantir que esses campos não sejam nulos ou que o DAO os aceite.
            novoCliente.setCep("");      
            novoCliente.setEndereco("");
            
            clienteDAO.salvarCliente(novoCliente);
            
            // 6. Redirecionamento de Sucesso
            request.getSession().setAttribute("sucesso", "Cadastro realizado com sucesso! Faça login para continuar.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (RuntimeException e) {
            e.printStackTrace();
            // Volta para a página de cadastro em caso de erro no DAO
            request.setAttribute("erro", "Falha ao finalizar o cadastro. Erro: " + e.getMessage());
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
        }
    }
}