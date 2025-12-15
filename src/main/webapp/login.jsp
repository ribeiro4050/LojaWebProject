<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Login Seguro - Loja de Roupas</title>
    <style>
        body { font-family: sans-serif; background-color: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; color: #333; }
        input { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button:hover { background-color: #0056b3; }
        .hidden { display: none; }
        #mensagem { color: red; text-align: center; margin-top: 10px; font-size: 14px; }
    </style>
</head>
<body>

    <div class="card">
        <h2>Acesso Restrito</h2>
        
        <div id="etapa1">
            <input type="email" id="email" placeholder="Seu E-mail" required>
            <input type="password" id="senha" placeholder="Sua Senha" required>
            <button onclick="fazerLogin()">Entrar</button>
        </div>

        <div id="etapa2" class="hidden">
            <p style="text-align:center; font-size:0.9em;">Enviamos um código para seu e-mail.</p>
            <input type="text" id="codigo" placeholder="Digite o código de 6 dígitos" maxlength="6">
            <button onclick="validar2FA()">Confirmar Código</button>
        </div>

        <p id="mensagem"></p>
    </div>

    <script>
        // FASE 1: Envia Login e Senha
        function fazerLogin() {
            let email = document.getElementById("email").value;
            let senha = document.getElementById("senha").value;
            let msg = document.getElementById("mensagem");

            msg.innerText = "Verificando credenciais...";
            msg.style.color = "blue";

            fetch('${pageContext.request.contextPath}/auth', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'acao=login&email=' + encodeURIComponent(email) + '&senha=' + encodeURIComponent(senha)
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'ok') {
                    // *** REMOVEMOS O BYPASS E RESTAURAMOS O 2FA FRONTE-END ***
                    
                    // CÓDIGO ORIGINAL (MOSTRAR 2FA) RESTAURADO:
                    document.getElementById("etapa1").classList.add("hidden");
                    document.getElementById("etapa2").classList.remove("hidden");
                    msg.innerText = "";
                    

                } else {
                    msg.innerText = data.mensagem;
                    msg.style.color = "red";
                }
            })
            .catch(err => console.error(err));
        }

        // FASE 2: Envia Código 2FA
        function validar2FA() {
            let codigo = document.getElementById("codigo").value;
            let msg = document.getElementById("mensagem");

            msg.innerText = "Validando código...";

            fetch('${pageContext.request.contextPath}/auth', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'acao=validar2fa&codigo=' + encodeURIComponent(codigo)
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'ok') {
                    // Redireciona para a vitrine se der certo
                    window.location.href = "${pageContext.request.contextPath}/home";
                } else {
                    msg.innerText = data.mensagem;
                    msg.style.color = "red";
                }
            });
        }
    </script>
</body>
</html>