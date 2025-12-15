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
            padding-top: 50px; 
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
        /* Estilo para erro AJAX */
        .error-message {
            color: red;
            font-size: 0.85em;
            margin-top: 5px;
            display: none; /* Escondido por padrão */
        }
        /* Altera a borda do input em caso de erro */
        .input-error {
            border-color: red !important;
        }
        .btn-register {
            width: 100%;
            background-color: #FF4500; 
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
        
        /* Layout para campos lado a lado (mantido) */
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
    
    <form id="cadastroForm" action="${pageContext.request.contextPath}/cadastro" method="POST">
        
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
            <input type="email" id="email" name="email" required placeholder="seu.email@exemplo.com"
                   onblur="validarDuplicidade(this, 'email')"> 
            <span id="email-error" class="error-message">Este E-mail já está cadastrado.</span>
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
                <input type="text" id="cpf" name="cpf" required placeholder="999.999.999-99"
                       onblur="validarDuplicidade(this, 'cpf')"> 
                <span id="cpf-error" class="error-message">Este CPF já está cadastrado.</span>
            </div>
        </div>

        <button type="submit" class="btn-register" id="btn-submit">
            Cadastrar
        </button>
    </form>
    
    <div class="login-link">
        Já tem cadastro? <a href="${pageContext.request.contextPath}/login.jsp">Faça Login</a>
    </div>
</div>

<script>
    const CONTEXT_PATH = "${pageContext.request.contextPath}";
    // Variável global para rastrear se o formulário está válido.
    // true = campo OK; false = campo duplicado/inválido.
    let isCpfValid = true;
    let isEmailValid = true;

    /**
     * Função AJAX para verificar se o CPF ou E-mail já existe no banco de dados.
     * @param {HTMLElement} inputElement O campo de input que disparou o evento (this).
     * @param {string} campo O nome da coluna a ser validada ('cpf' ou 'email').
     */
    function validarDuplicidade(inputElement, campo) {
        const valor = inputElement.value.trim();
        const errorElement = document.getElementById(campo + '-error');
        
        // 1. Limpeza e validação básica do valor
        if (valor.length === 0) {
            errorElement.style.display = 'none';
            inputElement.classList.remove('input-error');
            // Reseta o status global
            if (campo === 'cpf') isCpfValid = true;
            if (campo === 'email') isEmailValid = true;
            return;
        }

        // 2. Monta a URL para a requisição GET no Servlet de Cadastro
        // /cadastro?campo=cpf&valor=12345678900
        const url = `${CONTEXT_PATH}/cadastro?campo=${campo}&valor=${valor}`;

        // 3. Requisição AJAX usando Fetch
        fetch(url, {
            method: 'GET'
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Erro na comunicação com o servidor');
        })
        .then(data => {
            // A resposta é um JSON: {"existe": true} ou {"existe": false}
            const existe = data.existe;
            
            if (existe) {
                // Se existe, mostra o erro e marca o input
                errorElement.style.display = 'block';
                inputElement.classList.add('input-error');
                
                // Marca o status global como inválido
                if (campo === 'cpf') isCpfValid = false;
                if (campo === 'email') isEmailValid = false;
                
            } else {
                // Se não existe, esconde o erro
                errorElement.style.display = 'none';
                inputElement.classList.remove('input-error');
                
                // Marca o status global como válido
                if (campo === 'cpf') isCpfValid = true;
                if (campo === 'email') isEmailValid = true;
            }
            
            // 4. Atualiza o estado do botão de submissão
            toggleSubmitButton();
        })
        .catch(error => {
            console.error('Erro AJAX de validação:', error);
            // Em caso de erro de rede/servidor, é melhor não bloquear o usuário.
            errorElement.textContent = "Erro de validação. Tente novamente.";
            errorElement.style.display = 'block';
            
            if (campo === 'cpf') isCpfValid = false;
            if (campo === 'email') isEmailValid = false;
            toggleSubmitButton();
        });
    }
    
    /**
     * Habilita ou desabilita o botão de submissão com base nas validações AJAX.
     */
    function toggleSubmitButton() {
        const btn = document.getElementById('btn-submit');
        // O botão só fica habilitado se AMBOS os campos (CPF e Email) forem válidos
        if (isCpfValid && isEmailValid) {
            btn.disabled = false;
            btn.style.opacity = 1;
            btn.style.cursor = 'pointer';
        } else {
            btn.disabled = true;
            btn.style.opacity = 0.6;
            btn.style.cursor = 'not-allowed';
        }
    }
    
    // Inicializa o estado do botão ao carregar a página
    document.addEventListener('DOMContentLoaded', toggleSubmitButton);

</script>

</body>
</html>