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
    .navbar-links a {
        color: #333;
        text-decoration: none;
        padding: 8px 15px;
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
    /* Botão de Ação (Login/Cadastro) */
    .btn-login {
        background-color: #007bff;
        color: white;
        padding: 8px 15px;
        border-radius: 4px;
        text-decoration: none;
        font-weight: bold;
        transition: background-color 0.2s;
        /* Garantir que o botão do link e o button tenham o mesmo estilo */
        border: none;
        cursor: pointer;
    }
    .btn-login:hover {
        background-color: #0056b3;
    }
    /* Estilo para o botão de Logout */
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
    <a href="${pageContext.request.contextPath}/home" class="navbar-logo">LG Clothes</a>
    
    <div class="navbar-links">
        <a href="#">Lançamentos</a>
        <a href="#">Tênis</a>
        <a href="#">Roupas</a>
        <a href="#">Acessórios</a>
    </div>
    
    <div class="navbar-actions">
        
        <a href="${pageContext.request.contextPath}/carrinho" class="icon-cart" title="Ver Carrinho">
            🛒 
            <c:if test="${not empty sessionScope.carrinho}">
                <span class="cart-count">${fn:length(sessionScope.carrinho)}</span> 
            </c:if>
        </a>

        <%-- LÓGICA DE EXIBIÇÃO: LOGIN VS. LOGOUT --%>
        <c:choose>
            <c:when test="${not empty sessionScope.clienteLogado}">
                <%-- STATUS: LOGADO --%>
                
                <span class="welcome-message">
                    Olá, ${fn:split(sessionScope.clienteLogado.nome, ' ')[0]}!
                </span>
                
                <button onclick="fazerLogout()" class="btn-logout">Logout</button>
                
                <%-- O link "Cadastrar" é removido quando logado, mas você pode adicionar
                     um link para "Minha Conta" aqui se desejar. --%>
                
            </c:when>
            <c:otherwise>
                <%-- STATUS: DESLOGADO --%>
                
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-login">Login</a>
                <a href="${pageContext.request.contextPath}/cadastro.jsp" title="Criar uma nova conta">Cadastrar</a>
                
            </c:otherwise>
        </c:choose>
        
    </div>
</nav>

<%-- É fundamental incluir o script de logout se ele ainda não estiver em um arquivo JS global --%>
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
                    // Redireciona para /home (que recarregará a navbar deslogada)
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