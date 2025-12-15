package br.com.lojaroupa.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.lojaroupa.dao.PedidoDAO;
import br.com.lojaroupa.model.ItemCarrinho;
import br.com.lojaroupa.model.Pedido;


@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("carrinho") == null || 
            ((List<?>) request.getSession().getAttribute("carrinho")).isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }
        
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        @SuppressWarnings("unchecked")
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        
        if (carrinho == null || carrinho.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }

        // 1. CAPTURA DOS DADOS DETALHADOS DO FORMULÁRIO (JÁ ESTAVA CORRETO)
        
        String cep = request.getParameter("cep");
        String telefone = request.getParameter("telefone");
        String logradouro = request.getParameter("logradouro");
        String numero = request.getParameter("numero");
        String complemento = request.getParameter("complemento");
        String bairro = request.getParameter("bairro");
        String cidade = request.getParameter("cidade");
        String estado = request.getParameter("estado");
        String tipoFreteStr = request.getParameter("tipoFrete");
        
        int tipoFrete = 0;
        try {
            tipoFrete = Integer.parseInt(tipoFreteStr);
        } catch (NumberFormatException e) {
            tipoFrete = 0; 
        }
        
        // Concatena todos os campos de endereço para a coluna 'entregaEndereco' (JÁ ESTAVA CORRETO)
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro).append(", ").append(numero);
        
        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade).append("/").append(estado);
        sb.append(", CEP ").append(cep);
        
        String enderecoCompleto = sb.toString(); 

        // 2. ID DO CLIENTE (MANTIDO FIXO PARA TESTE)
        int idCliente = 1; 

        // 3. CÁLCULO DO VALOR TOTAL
        BigDecimal totalPedidoBD = BigDecimal.ZERO;
        for (ItemCarrinho item : carrinho) {
            totalPedidoBD = totalPedidoBD.add(item.getSubtotal());
        }
        
        // 4. Montar o Objeto Pedido (AGORA PREENCHENDO TODOS OS NOVOS CAMPOS!)
        
        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);
        
        // Mapeando a string completa para o campo 'entregaEndereco'
        pedido.setEntregaEndereco(enderecoCompleto); 
        
        // ***** LINHAS CHAVE QUE ESTAVAM FALTANDO / COMENTADAS *****
        pedido.setTipoFrete(tipoFrete); 
        pedido.setEntregaCEP(cep);
        pedido.setEntregaTelefone(telefone);
        // **********************************************************

        pedido.setValorTotal(totalPedidoBD);
        pedido.setItens(carrinho); 
        
        // 5. Salvar no Banco de Dados (DAO)
        PedidoDAO pedidoDAO = new PedidoDAO();
        try {
            pedido = pedidoDAO.salvarPedido(pedido);
            
            // 6. Finalização e Redirecionamento
            session.removeAttribute("carrinho");
            
            request.setAttribute("pedidoId", pedido.getIdPedido());
            request.setAttribute("totalPedido", pedido.getValorTotal().doubleValue());
            request.setAttribute("mensagemSucesso", "Parabéns! Seu pedido #" + pedido.getIdPedido() + " foi finalizado com sucesso!");
            
            request.getRequestDispatcher("/confirmacaoPedido.jsp").forward(request, response);

        } catch (RuntimeException e) {
            e.printStackTrace();
            request.setAttribute("erro", "Erro ao processar o pedido. Verifique o console do Eclipse e o banco de dados. Mensagem: " + e.getMessage());
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
        }
    }
}