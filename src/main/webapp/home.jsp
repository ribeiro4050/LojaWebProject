<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Adicionado para compatibilidade com o navbar --%>

<%-- Esta linha informa ao JSP que a lista de produtos virá do Servlet --%>
<jsp:useBean id="listaProdutos" scope="request" type="java.util.List"/>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Home - Loja de Roupas</title>
    <style>
        /* Ajuste no corpo para compensar o navbar se for fixo ou apenas por estética */
        body { 
            font-family: Arial, sans-serif; 
            background-color: #f8f9fa; 
            margin: 0; /* Adicionado para evitar margem dupla */
            padding-top: 50px; /* Adiciona espaço no topo para o navbar */
        }
        .container { 
            width: 90%; 
            margin: 40px auto; 
            padding-top: 0; /* Remove padding desnecessário */
        }
        h1 { text-align: center; color: #333; margin-bottom: 30px; }
        
        /* Layout Grid para os Cards */
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); 
            gap: 30px;
        }
        
        /* Estilo do Card */
        .product-card {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: transform 0.2s, box-shadow 0.2s;
            padding: 15px;
            text-align: center;
        }
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0,0,0,0.2);
        }
        .product-card img {
            width: 100%;
            height: 200px; 
            object-fit: cover;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
        }
        .product-title { font-size: 1.2em; font-weight: bold; margin-bottom: 5px; color: #007bff; }
        .product-brand { font-size: 0.9em; color: #666; margin-bottom: 5px; }
        .product-price { 
            font-size: 1.5em; 
            color: #28a745; 
            margin-top: 10px; 
            font-weight: bold; 
        }
        .product-details { font-size: 0.8em; color: #999; margin-bottom: 10px;}
        
        /* Novo estilo para o botão do carrinho */
        .cart-button {
            display: block;
            background-color: #007bff; /* Azul para adicionar ao carrinho */
            color: white;
            padding: 10px;
            text-align: center;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 10px; /* Reduzido para encaixar melhor */
            font-weight: bold;
            cursor: pointer;
            border: none;
        }
        .buy-button {
            display: block;
            background-color: #ffc107; /* Amarelo para detalhes */
            color: #333;
            padding: 10px;
            text-align: center;
            text-decoration: none;
            border-radius: 4px;
            margin-top: 8px; /* Ajustado para melhor espaçamento */
            font-weight: bold;
        }
    </style>
</head>
<body>

<%-- ADIÇÃO DO NAVBAR AQUI --%>
<jsp:include page="navbar.jsp"/>

<div class="container">
    <h1>Nossa Coleção</h1>

    <div class="product-grid">
        <%-- A tag JSTL c:forEach itera sobre a lista de produtos --%>
        <c:forEach var="produto" items="${listaProdutos}">
            <div class="product-card">
                
                <%-- CORRIGIDO: O caminho da imagem deve ser apenas /img/... --%>
                <img src="${pageContext.request.contextPath}/img/${produto.caminhoImagem}" alt="${produto.nome}">
                
                <p class="product-title">${produto.nome}</p>
                <p class="product-brand">Marca: ${produto.marca} | ${produto.modelo}</p>
                
                <div class="product-details">
                    <p>Cor: <span style="color:${produto.cor};">&#9632;</span> | Tamanho: ${produto.tamanho}</p>
                    <p>${produto.descricao}</p>
                </div>
                
                <p class="product-price">
                    <%-- Formata o preço como moeda --%>
                    <fmt:formatNumber value="${produto.preco}" type="currency" currencySymbol="R$" maxFractionDigits="2"/>
                </p>
                
                <form action="${pageContext.request.contextPath}/carrinho" method="POST" style="margin-top: 10px;">
                    
                    <input type="hidden" name="acao" value="adicionar">
                    <input type="hidden" name="idProduto" value="${produto.idProduto}">
                    
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                        <label for="qtd-${produto.idProduto}" style="font-weight: bold; font-size: 0.9em;">Qtd:</label>
                        <input type="number" 
                               id="qtd-${produto.idProduto}" 
                               name="quantidade" 
                               value="1" 
                               min="1" 
                               max="99" 
                               style="width: 50px; text-align: center; padding: 5px; border: 1px solid #ccc;">
                    </div>

                    <button type="submit" class="cart-button">
                        Adicionar ao Carrinho
                    </button>
                </form>
                
            </div>
        </c:forEach>
    </div>
    
    <c:if test="${empty listaProdutos}">
        <p style="text-align: center; margin-top: 50px;">Nenhum produto encontrado na loja. Verifique o banco de dados.</p>
    </c:if>

</div>

</body>
</html>