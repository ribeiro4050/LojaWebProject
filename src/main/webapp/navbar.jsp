<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
    /* Estilos da Barra de Navegação */
    .navbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-color: white;
        padding: 10px 5%;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .navbar-logo {
        font-size: 1.8em;
        font-weight: bold;
        color: #FF4500; 
        text-decoration: none;
    }
    .navbar-links {
        display: flex; /* Garante que os links da navbar links fiquem em linha */
        align-items: center;
        gap: 15px; /* Espaçamento entre os links */
    }
    .navbar-links a {
        color: #333;
        text-decoration: none;
        padding: 8px 0px; /* Reduzindo o padding vertical para não interferir no fluxo */
        margin: 0 5px;
        font-size: 1em;
        transition: color 0.2s;
    }
    .navbar-links a:hover {
        color: #FF4500;
    }
    .navbar-actions {
        display: flex;
        align-items: center;
        gap: 15px;
    }
    /* Botão de Ação (Login/Cadastro - Azul) */
    .btn-login {
        background-color: #007bff;
        color: white;
        padding: 8px 15px;
        border-radius: 4px;
        text-decoration: none;
        font-weight: bold;
        transition: background-color 0.2s;
        border: none;
        cursor: pointer;
    }
    .btn-login:hover {
        background-color: #0056b3;
    }
    /* Estilo para o botão de Logout (Vermelho) */
    .btn-logout {
        background-color: #dc3545; /* Vermelho */
        color: white;
        padding: 8px 15px;
        border-radius: 4px;
        font-weight: bold;
        transition: background-color 0.2s;
        border: none;
        cursor: pointer;
    }
    .btn-logout:hover {
        background-color: #c82333;
    }
    .welcome-message {
        color: #333;
        font-weight: 500;
        white-space: nowrap; /* Impede quebra de linha */
    }
    /* Estilo para o link de Gerenciamento (Funcionário) */
    .manager-link {
        color: #f0ad4e !important; /* Laranja/Amarelo */
        font-weight: bold;
    }
    .manager-link:hover {
        color: #ec971f !important;
    }
    /* Ícones */
    .icon-cart {
        font-size: 1.5em;
        text-decoration: none;
        color: #333;
        position: relative;
    }
    .icon-cart:hover {
        color: #FF4500;
    }
    .cart-count {
        position: absolute;
        top: -5px;
        right: -10px;
        background-color: #FF4500;
        color: white;
        border-radius: 50%;
        padding: 2px 6px;
        font-size: 0.7em;
        line-height: 1;
    }
</style>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/home" class="navbar-logo">LG clothes</a>
    
    <div class="navbar-links">
        <a href="#">Lançamentos</a>
        <a href="#">Tênis</a>
        <a href="#">Roupas</a>
        <a href="#">Acessórios</a>
        
        <%-- Lógica para exibir link de Gerenciamento APENAS para Funcionários --%>
        <c:if test="${not empty sessionScope.funcionarioLogado}">
            <a href="${pageContext.request.contextPath}/gerenciar-produtos" class="manager-link">
                Painel de Produtos
            </a>
        </c:if>
    </div>
    
    <div class="navbar-actions">
        
        <a href="${pageContext.request.contextPath}/carrinho" class="icon-cart" title="Ver Carrinho">
            🛒 
            <c:if test="${not empty sessionScope.carrinho}">
                <span class="cart-count">${fn:length(sessionScope.carrinho)}</span> 
            </c:if>
        </a>

        <%-- LÓGICA DE EXIBIÇÃO PRINCIPAL: FUNCIONÁRIO VS. CLIENTE VS. DESLOGADO --%>
        <c:choose>
            
            <%-- ESTADO 1: FUNCIONÁRIO LOGADO --%>
            <c:when test="${not empty sessionScope.funcionarioLogado}">
                <span class="welcome-message">
                    Olá, ${fn:split(sessionScope.funcionarioLogado.nome, ' ')[0]}! (Funcionario)
                </span>
                
                <button onclick="fazerLogout()" class="btn-logout">Logout</button>
            </c:when>
            
            <%-- ESTADO 2: CLIENTE LOGADO --%>
            <c:when test="${not empty sessionScope.clienteLogado}">
                <span class="welcome-message">
                    Olá, ${fn:split(sessionScope.clienteLogado.nome, ' ')[0]}!
                </span>
                
                <button onclick="fazerLogout()" class="btn-logout">Logout</button>
            </c:when>
            
            <%-- ESTADO 3: DESLOGADO --%>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-login">Login</a>
                <a href="${pageContext.request.contextPath}/cadastro.jsp" title="Criar uma nova conta">Cadastrar</a>
            </c:otherwise>
        </c:choose>
        
    </div>
</nav>

<%-- Script de Logout (Usando AJAX para chamar o /auth com acao=logout) --%>
<script>
    function fazerLogout() {
        if (confirm("Tem certeza que deseja sair da sua conta?")) {
            fetch('${pageContext.request.contextPath}/auth', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'acao=logout'
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'ok') {
                    // Após logout bem-sucedido, redireciona para a home para recarregar a navbar
                    window.location.href = "${pageContext.request.contextPath}/home"; 
                } else {
                    alert("Falha ao realizar logout.");
                }
            })
            .catch(err => {
                console.error("Erro no fetch de logout:", err);
                alert("Erro de comunicação com o servidor.");
            });
        }
    }
</script>