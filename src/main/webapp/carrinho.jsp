<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- 
    1. Obtém a lista de itens do carrinho que o CarrinhoServlet guardou na Sessão (sessionScope.carrinho) 
--%>
<c:set var="carrinho" value="${sessionScope.carrinho}" />

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Meu Carrinho de Compras</title>
    <style>
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
		    display: inline-block; /* Permite que o link tenha padding e largura */
		    background-color: #dc3545; /* Vermelho padrão do Bootstrap para perigo/excluir */
		    color: white;
		    padding: 6px 12px;
		    text-align: center;
		    text-decoration: none;
		    border-radius: 4px; /* Cantos arredondados */
		    font-size: 0.9em; /* Um pouco menor para caber na tabela */
		    border: none;
		    transition: background-color 0.2s;
		}

		.remove-btn:hover {
		    background-color: #c82333; /* Vermelho mais escuro no hover */
		}
    </style>
</head>
<body>

<div class="container">
    <h1>Seu Carrinho de Compras</h1>

    <c:choose>
        <%-- Caso 1: Carrinho vazio --%>
        <c:when test="${empty carrinho || carrinho.size() == 0}">
            <p style="text-align: center; font-size: 1.2em; color: #dc3545;">
                Seu carrinho está vazio.
                <br><br>
                <a href="${pageContext.request.contextPath}/home" class="continue-btn" style="background-color: #007bff;">Voltar às compras</a>
            </p>
        </c:when>

        <%-- Caso 2: Carrinho tem itens --%>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Produto</th>
                        <th>Preço Unitário</th>
                        <th>Quantidade</th>
                        <th>Subtotal</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Inicializa a variável para o Total Geral --%>
                    <c:set var="totalGeral" value="${0}" />
                    
                    <%-- Loop para exibir cada item no carrinho --%>
                    <c:forEach var="item" items="${carrinho}">
                        <tr>
                            <td>
                                <img src="${pageContext.request.contextPath}/img/${item.produto.caminhoImagem}" alt="${item.produto.nome}" style="width: 50px; height: 50px; object-fit: cover; margin-right: 10px;">
                                ${item.produto.nome}
                            </td>
                            <td>
                                <fmt:formatNumber value="${item.produto.preco}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                            </td>
                            <td>
                                ${item.quantidade}
                            </td>
                            <td>
                                <%-- O getSubtotal() da classe ItemCarrinho é chamado aqui --%>
                                <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/carrinho?acao=remover&idProduto=${item.produto.idProduto}" class="remove-btn">Remover</a>
                            </td>
                        </tr>
                        <%-- Acumula o subtotal de cada item ao total geral --%>
                        <c:set var="totalGeral" value="${totalGeral + item.subtotal}" />
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3" style="text-align: right;">TOTAL GERAL:</td>
                        <td>
                            <fmt:formatNumber value="${totalGeral}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                        </td>
                        <td></td>
                    </tr>
                </tfoot>
            </table>
            
            <div class="actions">
                <a href="${pageContext.request.contextPath}/home" class="continue-btn">Continuar Comprando</a>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">Finalizar Compra</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>