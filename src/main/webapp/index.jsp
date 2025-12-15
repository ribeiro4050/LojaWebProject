<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Loja de Roupas - Painel de Produtos</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; background-color: #f4f4f9; }
        
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ddd; padding-bottom: 10px; margin-bottom: 20px; }
        h1 { color: #333; margin: 0; }
        
        table { width: 100%; border-collapse: collapse; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #007bff; color: white; text-transform: uppercase; font-size: 0.9em; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f1f1f1; }
        
        /* Botões */
        .btn { padding: 10px 20px; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; transition: background 0.3s; }
        .btn-refresh { background-color: #28a745; }
        .btn-refresh:hover { background-color: #218838; }
        
        .btn-logout { background-color: #dc3545; } /* Vermelho para sair */
        .btn-logout:hover { background-color: #c82333; }
        
        .preco { color: #d9534f; font-weight: bold; }
    </style>
</head>
<body>

    <div class="header">
        <h1>Painel de Gerenciamento de Produtos</h1>
        <button class="btn btn-logout" onclick="fazerLogout()"> Sair</button>
    </div>
    
    <button class="btn btn-refresh" onclick="carregarProdutos()"> Atualizar Lista</button>
    <p id="status" style="color: gray; font-size: 0.9em;">Carregando...</p>

    <table id="tabelaProdutos">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Marca</th>
                <th>Modelo</th>
                <th>Tamanho</th>
                <th>Preço</th>
                <th>Estoque</th>
            </tr>
        </thead>
        <tbody>
            </tbody>
    </table>

    <script>
        // Função para Buscar Produtos
        function carregarProdutos() {
            document.getElementById("status").innerText = "Carregando dados...";
            // URL para o ProdutoController que retorna JSON
            const url = '${pageContext.request.contextPath}/produtos'; 

            fetch(url) 
                .then(response => {
                    // Se der erro 401 ou 403, pode ser sessão expirada
                    if (response.redirected) {
                        window.location.href = response.url;
                        return;
                    }
                    if (!response.ok) throw new Error("Erro HTTP: " + response.status);
                    return response.json();
                })
                .then(data => {
                    let tbody = document.querySelector("#tabelaProdutos tbody");
                    if (!tbody) return;

                    tbody.innerHTML = ""; 
                    
                    if(!data || data.length === 0) {
                        document.getElementById("status").innerText = "Nenhum produto encontrado.";
                        return;
                    }

                    let htmlCompleto = "";
                    
                    data.forEach(p => {
                        let precoNumerico = p.preco ? p.preco : 0;
                        let precoFormatado = parseFloat(precoNumerico).toFixed(2).replace('.', ',');

                        // Usamos \${ para escapar do JSP e funcionar no JS
                        htmlCompleto += `
                            <tr>
                                <td>\${p.idProduto}</td>
                                <td>\${p.nome}</td>
                                <td>\${p.marca}</td>
                                <td>\${p.modelo}</td>
                                <td>\${p.tamanho}</td>
                                <td class="preco">R$ \${precoFormatado}</td>
                                <td>\${p.ativo === '1' ? 'Em Estoque' : 'Inativo'}</td>
                            </tr>
                        `;
                    });

                    tbody.innerHTML = htmlCompleto;
                    document.getElementById("status").innerText = "Lista atualizada! (" + data.length + " itens)";
                })
                .catch(error => {
                    console.error('Erro:', error);
                    document.getElementById("status").innerText = "Erro ao carregar. Verifique o ProdutoController.";
                });
        }

        // Função para Sair (Logout) - CORRIGIDA
        function fazerLogout() {
            if(confirm("Deseja realmente sair?")) {
                // Chama o Servlet /auth via POST
                fetch('${pageContext.request.contextPath}/auth', { 
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: 'acao=logout' // Envia a ação no corpo (Melhor prática para POST)
                })
                .then(() => {
                    // Redireciona para a HOME, onde o navbar será recarregado como deslogado
                    window.location.href = '${pageContext.request.contextPath}/home';
                });
            }
        }

        window.onload = carregarProdutos;
    </script>

</body>
</html>