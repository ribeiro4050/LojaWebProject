package br.com.lojaroupa.controller;

import br.com.lojaroupa.model.Produto;
import br.com.lojaroupa.model.ItemCarrinho;
import br.com.lojaroupa.dao.ProdutoDAO;

import java.io.IOException;
import java.io.PrintWriter; // Importação necessária para enviar JSON
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/carrinho") 
public class CarrinhoServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L; // Adicionado para boas práticas

    /**
     * Trata requisições GET. Usado para:
     * 1. Exibir a página do carrinho (default).
     * 2. Processar a ação de 'remover' item (via link URL).
     */
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");
        
        if ("remover".equals(acao)) {
            removerItem(request, response);
        } else {
            request.getRequestDispatcher("/carrinho.jsp").forward(request, response);
        }
    }
    
    /**
     * Trata requisições POST. Usado para:
     * 1. Processar a ação de 'adicionar' item (formulário home.jsp).
     * 2. Processar a ação de 'atualizar' item (via AJAX no carrinho.jsp).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); 
        
        String acao = request.getParameter("acao");
        
        if (acao == null) {
            acao = "adicionar"; // Ação padrão
        }

        switch (acao) {
            case "adicionar":
                adicionarItem(request, response);
                break;
            case "atualizar":
                atualizarItem(request, response); // NOVO: Chama a função de atualização AJAX
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/carrinho");
        }
    }

    // ===========================================
    // --- NOVO MÉTODO: Lógica de Atualização AJAX ---
    // ===========================================
    @SuppressWarnings("unchecked")
    private void atualizarItem(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Parâmetros necessários
        String idProdutoStr = request.getParameter("idProduto");
        String novaQuantidadeStr = request.getParameter("quantidade");
        
        // Configura a resposta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // 1. Validação básica de parâmetros
        if (idProdutoStr == null || novaQuantidadeStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"erro\", \"mensagem\": \"ID ou Quantidade inválidos.\"}");
            out.flush();
            return;
        }

        try {
            int idProduto = Integer.parseInt(idProdutoStr);
            int novaQuantidade = Integer.parseInt(novaQuantidadeStr);
            
            HttpSession session = request.getSession();
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
            
            boolean sucesso = false;

            if (carrinho != null) {
                // 2. Procura e atualiza o item
                ItemCarrinho itemParaRemover = null;
                for (ItemCarrinho item : carrinho) {
                    if (item.getProduto().getIdProduto() == idProduto) {
                        
                        if (novaQuantidade > 0) {
                            // Atualiza a quantidade
                            item.setQuantidade(novaQuantidade);
                            sucesso = true;
                        } else {
                            // Quantidade zero ou negativa, marca para remoção
                            itemParaRemover = item;
                            sucesso = true;
                        }
                        break;
                    }
                }
                
                // 3. Remove o item se a quantidade for zero
                if(itemParaRemover != null) {
                    carrinho.remove(itemParaRemover);
                }
                
                // 4. Salva o carrinho atualizado na sessão
                session.setAttribute("carrinho", carrinho); 
            }
            
            // 5. Envia a resposta de sucesso
            if (sucesso) {
                out.print("{\"status\": \"ok\", \"mensagem\": \"Carrinho atualizado.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\": \"erro\", \"mensagem\": \"Item não encontrado no carrinho.\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"erro\", \"mensagem\": \"Formato de número incorreto.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\": \"erro\", \"mensagem\": \"Erro interno no servidor.\"}");
        } finally {
            out.flush();
        }
    }
    
    // ===========================================
    // --- Lógica de Adicionar (mantida) ---
    // ===========================================
    @SuppressWarnings("unchecked")
    private void adicionarItem(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        // 1. Obter e validar parâmetros
        String idProdutoStr = request.getParameter("idProduto");
        String quantidadeStr = request.getParameter("quantidade");
        
        if (idProdutoStr == null || quantidadeStr == null) {
             response.sendRedirect(request.getContextPath() + "/home");
             return;
        }

        int idProduto = Integer.parseInt(idProdutoStr);
        int quantidade = Integer.parseInt(quantidadeStr);
        
        // 2. Buscar o Produto no banco de dados
        ProdutoDAO produtoDAO = new ProdutoDAO(); 
        Produto produto = produtoDAO.buscarPorId(idProduto); 

        if (produto != null && quantidade > 0) {
            
            // 3. Acessar a Sessão HTTP para obter/criar o carrinho
            HttpSession session = request.getSession();
            
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
            
            if (carrinho == null) {
                carrinho = new ArrayList<>();
            }
            
            // 4. Verificar se o produto JÁ EXISTE no carrinho
            boolean encontrado = false;
            for (ItemCarrinho item : carrinho) {
                if (item.getProduto().getIdProduto() == idProduto) {
                    item.setQuantidade(item.getQuantidade() + quantidade);
                    encontrado = true;
                    break;
                }
            }
            
            // 5. Se não encontrado, cria um novo ItemCarrinho
            if (!encontrado) {
                ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade);
                carrinho.add(novoItem);
            }
            
            // 6. Salvar a lista atualizada de volta na sessão
            session.setAttribute("carrinho", carrinho);
            
            // Redireciona o usuário para a página de visualização do carrinho
            response.sendRedirect(request.getContextPath() + "/carrinho");
            
        } else {
            // Se o produto for inválido, volta para a home
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    // ===========================================
    // --- Lógica de Remover (mantida, mas sem o supresswarnings) ---
    // ===========================================
    @SuppressWarnings("unchecked")
    private void removerItem(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // 1. Obter o ID do produto a ser removido
        String idProdutoStr = request.getParameter("idProduto");
        
        if (idProdutoStr != null) {
            int idProduto = Integer.parseInt(idProdutoStr);
            HttpSession session = request.getSession();
            
            // 2. Obter o carrinho da sessão
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
            
            if (carrinho != null) {
                // 3. Iterar e remover o item
                ItemCarrinho itemParaRemover = null;
                for (ItemCarrinho item : carrinho) {
                    if (item.getProduto().getIdProduto() == idProduto) {
                        itemParaRemover = item;
                        break;
                    }
                }
                
                if (itemParaRemover != null) {
                    carrinho.remove(itemParaRemover);
                    
                    // 4. Salvar a lista atualizada de volta na sessão
                    session.setAttribute("carrinho", carrinho);
                }
            }
        }
        
        // 5. Redireciona de volta para a página do carrinho para mostrar a atualização
        response.sendRedirect(request.getContextPath() + "/carrinho");
    }
}