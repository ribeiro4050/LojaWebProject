<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmação de Pedido</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .success-box { border: 1px solid #d4edda; background-color: #d1e7dd; color: #0f5132; padding: 20px; border-radius: 5px; margin-top: 20px; }
        .details { margin-top: 15px; border-top: 1px solid #eee; padding-top: 10px; }
        .details p { margin: 5px 0; }
        .home-link { display: inline-block; margin-top: 20px; padding: 10px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>

    <h1>🛒 Finalização da Compra</h1>

    <div class="success-box">
        <h2><c:out value="${mensagemSucesso}" /></h2>
        <p>Agradecemos a sua preferência! Seu pedido será processado em breve.</p>
    </div>

    <div class="details">
        <h3>Detalhes:</h3>
        <p><strong>Número do Pedido:</strong> <c:out value="${pedidoId}" /></p>
        <p><strong>Total:</strong> R$ <c:out value="${totalPedido}" /></p>
    </div>

    <a href="${pageContext.request.contextPath}/home" class="home-link">Voltar para a Loja</a>

</body>
</html>