package br.com.lojaroupa.controller;

import br.com.lojaroupa.model.Produto;
import br.com.lojaroupa.model.ItemCarrinho;
import br.com.lojaroupa.dao.ProdutoDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/carrinho") // Mapeia o Servlet para a URL /carrinho
public class CarrinhoServlet extends HttpServlet {
    
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
            // Se a URL contém ?acao=remover, chama a lógica de remoção
            removerItem(request, response);
        } else {
            // Se não for uma ação específica (apenas /carrinho), exibe o JSP
            request.getRequestDispatcher("/carrinho.jsp").forward(request, response);
        }
    }
    
    /**
     * Trata requisições POST. Usado principalmente para:
     * 1. Processar a ação de 'adicionar' item (via formulário da home.jsp).
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
            // TODO: Implementar 'atualizar' aqui
            default:
                // Redireciona para o GET que exibirá a página
                response.sendRedirect(request.getContextPath() + "/carrinho");
        }
    }

    // ===========================================
    // --- Lógica de Negócio do Carrinho ---
    // ===========================================
    
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
        
        // 2. Buscar o Produto no banco de dados (Necessário para obter preço e dados completos)
        ProdutoDAO produtoDAO = new ProdutoDAO(); 
        Produto produto = produtoDAO.buscarPorId(idProduto); 

        if (produto != null && quantidade > 0) {
            
            // 3. Acessar a Sessão HTTP para obter/criar o carrinho
            HttpSession session = request.getSession();
            
            // 
            // A lista do carrinho é guardada como um atributo de sessão
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
            
            if (carrinho == null) {
                // Se o carrinho ainda não existe na sessão, cria uma nova lista
                carrinho = new ArrayList<>();
            }
            
            // 4. Verificar se o produto JÁ EXISTE no carrinho
            boolean encontrado = false;
            for (ItemCarrinho item : carrinho) {
                if (item.getProduto().getIdProduto() == idProduto) {
                    // Se encontrado, apenas incrementa a quantidade
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
                // Usamos um loop for aprimorado, mas a remoção é feita fora dele
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