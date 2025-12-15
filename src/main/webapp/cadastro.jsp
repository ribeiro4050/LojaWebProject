<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Cliente - ARTWALK Inspire</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background-color: #f8f9fa; 
            margin: 0; 
            padding-top: 50px; /* Espaço para o navbar */
        }
        .register-container {
            width: 90%;
            max-width: 500px;
            margin: 40px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 25px;
            font-size: 1.8em;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        .form-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 1em;
        }
        .btn-register {
            width: 100%;
            background-color: #FF4500; /* Cor de destaque */
            color: white;
            padding: 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1.1em;
            font-weight: bold;
            margin-top: 15px;
            transition: background-color 0.2s;
        }
        .btn-register:hover {
            background-color: #cc3700;
        }
        .login-link {
            text-align: center;
            margin-top: 20px;
            font-size: 0.9em;
        }
        .login-link a {
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }
        .login-link a:hover {
            text-decoration: underline;
        }
        
        /* Layout para campos lado a lado (ex: Telefone e CPF) */
        .form-row {
            display: flex;
            gap: 15px;
        }
        .form-row .form-group {
            flex: 1;
        }
    </style>
</head>
<body>

<%-- Inclui a barra de navegação --%>
<jsp:include page="navbar.jsp"/>

<div class="register-container">
    <h1>Crie sua Conta</h1>
    
    <form action="${pageContext.request.contextPath}/cadastro" method="POST">
        
        <%-- Mensagens de feedback (se o Servlet de Cadastro enviar) --%>
        <c:if test="${not empty requestScope.erro}">
            <p style="color: red; text-align: center;">${requestScope.erro}</p>
        </c:if>
        <c:if test="${not empty requestScope.sucesso}">
            <p style="color: green; text-align: center;">${requestScope.sucesso}</p>
        </c:if>
        
        <div class="form-group">
            <label for="nome">Nome Completo</label>
            <input type="text" id="nome" name="nome" required placeholder="Seu nome">
        </div>

        <div class="form-group">
            <label for="email">E-mail</label>
            <input type="email" id="email" name="email" required placeholder="seu.email@exemplo.com">
        </div>

        <div class="form-group">
            <label for="senha">Senha</label>
            <input type="password" id="senha" name="senha" required placeholder="Mínimo 6 caracteres">
        </div>
        
        <div class="form-group">
            <label for="confirmarSenha">Confirmar Senha</label>
            <input type="password" id="confirmarSenha" name="confirmarSenha" required placeholder="Repita a senha">
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="telefone">Telefone</label>
                <input type="text" id="telefone" name="telefone" required placeholder="(99) 99999-9999">
            </div>
            
            <div class="form-group">
                <label for="cpf">CPF</label>
                <input type="text" id="cpf" name="cpf" required placeholder="999.999.999-99">
            </div>
        </div>

        <button type="submit" class="btn-register">
            Cadastrar
        </button>
    </form>
    
    <div class="login-link">
        Já tem cadastro? <a href="${pageContext.request.contextPath}/login.jsp">Faça Login</a>
    </div>
</div>

</body>
</html>