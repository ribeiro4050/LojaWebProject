<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="carrinho" value="${sessionScope.carrinho}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Meu Carrinho de Compras</title>
    <style>
        /* Estilos CSS (mantidos do seu original) */
        body { font-family: Arial, sans-serif; background-color: #f8f9fa; }
        .container { width: 90%; margin: 40px auto; }
        h1 { text-align: center; color: #333; margin-bottom: 30px; }
        table { 
            width: 100%; 
            border-collapse: collapse; 
            margin-top: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            background-color: white;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #007bff;
            color: white;
            font-weight: bold;
        }
        tfoot td {
            font-size: 1.2em;
            font-weight: bold;
            background-color: #e9ecef;
        }
        .actions {
            text-align: right;
            margin-top: 20px;
        }
        .actions a {
            text-decoration: none;
            padding: 8px 15px;
            border-radius: 4px;
            font-weight: bold;
            margin-left: 10px;
        }
        .continue-btn {
            background-color: #1074cc;
            color: white;
        }
        .checkout-btn {
            background-color: #28a745;
            color: white;
        }
        .remove-btn {
            display: inline-block; 
            background-color: #dc3545; 
            color: white;
            padding: 6px 12px;
            text-align: center;
            text-decoration: none;
            border-radius: 4px;
            font-size: 0.9em; 
            border: none;
            cursor: pointer; /* Adicionado cursor */
            transition: background-color 0.2s;
        }

        .remove-btn:hover {
            background-color: #c82333;
        }
        .quantity-input {
            width: 60px;
            padding: 5px;
            text-align: center;
        }
        /* Novo estilo para exibir mensagens */
        #message-area {
            color: green;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Seu Carrinho de Compras</h1>
    <div id="message-area"></div>

    <c:choose>
        <%-- Caso 1: Carrinho vazio --%>
        <c:when test="${empty carrinho || carrinho.size() == 0}">
            <p style="text-align: center; font-size: 1.2em; color: #dc3545;" id="empty-cart-message">
                Seu carrinho está vazio.
                <br><br>
                <a href="${contextPath}/home" class="continue-btn" style="background-color: #007bff;">Voltar às compras</a>
            </p>
        </c:when>

        <%-- Caso 2: Carrinho tem itens --%>
        <c:otherwise>
            <table id="cart-table">
                <thead>
                    <tr>
                        <th>Produto</th>
                        <th>Preço Unitário</th>
                        <th>Quantidade</th>
                        <th>Subtotal</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="cart-body">
                    <%-- Loop para exibir cada item no carrinho --%>
                    <c:forEach var="item" items="${carrinho}">
                        <tr data-id="${item.produto.idProduto}">
                            <td>
                                <img src="${contextPath}/img/${item.produto.caminhoImagem}" alt="${item.produto.nome}" style="width: 50px; height: 50px; object-fit: cover; margin-right: 10px;">
                                ${item.produto.nome}
                            </td>
                            <td>
                                <span class="unit-price" data-price="${item.produto.preco}">
                                    <fmt:formatNumber value="${item.produto.preco}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                                </span>
                            </td>
                            <td>
                                <%-- CAMPO DE QUANTIDADE EDITÁVEL --%>
                                <input type="number" 
                                       value="${item.quantidade}" 
                                       min="1" 
                                       data-idproduto="${item.produto.idProduto}"
                                       class="quantity-input"
                                       onchange="updateCartItem(this)">
                            </td>
                            <td>
                                <span class="subtotal-display">
                                    <%-- O getSubtotal() da classe ItemCarrinho é chamado aqui --%>
                                    <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                                </span>
                            </td>
                            <td>
                                <a href="${contextPath}/carrinho?acao=remover&idProduto=${item.produto.idProduto}" class="remove-btn">Remover</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" style="text-align: right;">TOTAL GERAL:</td>
                        <td id="total-geral-display">
                            <%-- Cálculo do total feito no Script, mas exibido aqui inicialmente --%>
                            <c:set var="totalGeral" value="${0}" />
                            <c:forEach var="item" items="${carrinho}">
                                <c:set var="totalGeral" value="${totalGeral + item.subtotal}" />
                            </c:forEach>
                            <fmt:formatNumber value="${totalGeral}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                        </td>
                        <td></td>
                    </tr>
                </tfoot>
            </table>
            
            <div class="actions">
                <a href="${contextPath}/home" class="continue-btn">Continuar Comprando</a>
                <a href="${contextPath}/checkout" class="checkout-btn">Finalizar Compra</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    const CONTEXT_PATH = "${contextPath}";
    
    /**
     * Formata um número como moeda brasileira (R$ X.XXX,XX).
     */
    function formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL',
            minimumFractionDigits: 2
        }).format(value);
    }
    
    /**
     * Calcula e exibe o Total Geral do carrinho.
     */
    function calculateTotal() {
        let total = 0;
        // Seleciona todos os elementos que exibem o subtotal
        const subtotals = document.querySelectorAll('.subtotal-display');
        
        subtotals.forEach(span => {
            // Remove a formatação R$ e usa a classe Intl.NumberFormat para parsear
            // Nota: Esta abordagem é mais simples do que tentar parsear a string formatada
            // Vamos depender de recalcular o subtotal a partir do input de quantidade
            
            // Uma forma mais simples: recalcular o total a partir dos inputs e preços unitários
            const rows = document.querySelectorAll('#cart-body tr');
            
            rows.forEach(row => {
                const quantity = parseFloat(row.querySelector('.quantity-input').value);
                // Assume que o preço está no atributo data-price
                const price = parseFloat(row.querySelector('.unit-price').dataset.price);
                total += quantity * price;
            });
        });
        
        document.getElementById('total-geral-display').textContent = formatCurrency(total);

        // Se o total for zero, exibe a mensagem de carrinho vazio
        if (total === 0) {
             const cartTable = document.getElementById('cart-table');
             const emptyMessage = document.getElementById('empty-cart-message');

             if (cartTable) cartTable.style.display = 'none';
             if (emptyMessage) emptyMessage.style.display = 'block';
        }
    }
    
    /**
     * Função AJAX para enviar a nova quantidade para o Servlet.
     */
    function updateCartItem(inputElement) {
        const idProduto = inputElement.dataset.idproduto;
        let novaQuantidade = parseInt(inputElement.value);
        const row = inputElement.closest('tr'); // Pega a linha da tabela
        const priceElement = row.querySelector('.unit-price');
        const price = parseFloat(priceElement.dataset.price);
        const messageArea = document.getElementById('message-area');

        // Garante que a quantidade mínima é 1 para o input, mas permite 0 para remoção no código
        if (novaQuantidade < 0) {
             novaQuantidade = 0; // Se o usuário digitar negativo, forçamos 0 para acionar a remoção
        }

        // 1. Prepara os dados para envio
        const data = new URLSearchParams();
        data.append('acao', 'atualizar');
        data.append('idProduto', idProduto);
        data.append('quantidade', novaQuantidade);
        
        // Exibe mensagem de carregamento
        messageArea.textContent = "Atualizando carrinho...";

        // 2. Envia a requisição AJAX (fetch)
        fetch(CONTEXT_PATH + '/carrinho', {
            method: 'POST',
            body: data,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Erro na requisição: ' + response.statusText);
        })
        .then(data => {
            if (data.status === 'ok') {
                
                // 3. ATUALIZA O FRONTEND
                if (novaQuantidade === 0) {
                    // Se a quantidade é zero, remove a linha da tabela
                    row.remove();
                    messageArea.textContent = "Item removido com sucesso.";
                } else {
                    // Calcula e atualiza o subtotal
                    const subtotal = novaQuantidade * price;
                    row.querySelector('.subtotal-display').textContent = formatCurrency(subtotal);
                    
                    messageArea.textContent = "Quantidade atualizada.";
                }
                
                // 4. Recalcula o total
                calculateTotal();

            } else {
                // Trata erro retornado pelo Servlet
                messageArea.textContent = "Erro: " + data.mensagem;
                console.error('Erro do Servlet:', data.mensagem);
            }
        })
        .catch(error => {
            messageArea.textContent = "Erro ao se comunicar com o servidor.";
            console.error('AJAX Error:', error);
            // Reverte o valor do input para evitar confusão se houver falha
            // Não implementado aqui para simplicidade, mas seria uma boa prática.
        });
    }

    // Inicializa o cálculo total ao carregar a página
    document.addEventListener('DOMContentLoaded', calculateTotal);
    
</script>

</body>
</html>