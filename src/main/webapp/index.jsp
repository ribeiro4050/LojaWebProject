<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Loja de Roupas - Painel de Produtos</title>
    <style>
        /* ... Estilos omitidos por brevidade ... */
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; background-color: #f4f4f9; }
        
        .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #ddd; padding-bottom: 10px; margin-bottom: 20px; }
        h1 { color: #333; margin: 0; }
        
        /* Estilo do Formulário */
        #formProduto { margin-bottom: 30px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        #formProduto input, #formProduto select { padding: 10px; margin: 5px; border: 1px solid #ddd; border-radius: 4px; width: 180px; }

        /* Filtro e Paginação */
        .controls { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
        #filtroContainer input { padding: 10px; border: 1px solid #ddd; border-radius: 4px; width: 300px; }
        
        .pagination { display: flex; justify-content: center; padding: 20px 0; }
        .pagination button { 
            padding: 8px 12px; 
            margin: 0 4px; 
            background-color: #f0f0f0; 
            border: 1px solid #ccc; 
            border-radius: 4px; 
            cursor: pointer; 
            transition: background-color 0.2s; 
        }
        .pagination button:hover:not(:disabled) { background-color: #e0e0e0; }
        .pagination button.active { background-color: #007bff; color: white; border-color: #007bff; }
        .pagination button:disabled { cursor: not-allowed; opacity: 0.6; }


        table { width: 100%; border-collapse: collapse; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1); margin-top: 20px;}
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; font-size: 0.9em; }
        th { background-color: #007bff; color: white; text-transform: uppercase; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f1f1f1; }
        
        /* Botões */
        .btn { padding: 8px 15px; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 14px; transition: background 0.3s; margin-right: 5px;}
        .btn-refresh { background-color: #28a745; }
        .btn-refresh:hover { background-color: #218838; }
        
        .btn-logout { background-color: #dc3545; } 
        .btn-logout:hover { background-color: #c82333; }

        /* Botões de CRUD */
        .btn-edit { background-color: #ffc107; }
        .btn-edit:hover { background-color: #e0a800; }

        .btn-delete { background-color: #dc3545; }
        .btn-delete:hover { background-color: #c82333; }

        .btn-save { background-color: #007bff; }
        .btn-cancel { background-color: #6c757d; display: none; } 

        .preco { color: #d9534f; font-weight: bold; }
        /* Estilos para mensagens de status */
        .mensagem { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>

    <div class="header">
        <h1>Painel de Gerenciamento de Produtos</h1>
        <button class="btn btn-logout" onclick="fazerLogout()"> Sair</button>
    </div>
    
    <div id="mensagem" class="mensagem" style="display:none;"></div>
    
    <form id="formProduto">
        <h2><span id="formTitle">Adicionar Novo</span> Produto</h2>
        <input type="hidden" id="idProduto" value="0">
        
        <input type="text" id="nome" placeholder="Nome" required>
        <input type="text" id="marca" placeholder="Marca" required>
        <input type="text" id="modelo" placeholder="Modelo">
        <input type="text" id="tamanho" placeholder="Tamanho">
        <input type="number" id="preco" placeholder="Preço (ex: 99.90)" step="0.01" required>
        <input type="number" id="estoqueMinimo" placeholder="Estoque Inicial" required> 
        <select id="ativo">
            <option value="1">Ativo</option>
            <option value="0">Inativo</option>
        </select>
        
        <button type="submit" class="btn btn-save" id="btnSalvar">Salvar Produto</button>
        <button type="button" class="btn btn-cancel" id="btnCancelar">Cancelar Edição</button>
    </form>
    
    <div class="controls">
        <div id="filtroContainer">
            <input type="text" id="inputFiltro" placeholder="Buscar por Nome, Marca ou Modelo..." 
                   onkeyup="debouncedCarregarProdutos()">
        </div>
        <button class="btn btn-refresh" onclick="carregarProdutos()"> <i class="fas fa-sync"></i> Recarregar Lista</button>
    </div>
    
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
                <th>Estoque Atual</th>
                <th>Ativo</th>
                <th>Ações</th> </tr>
        </thead>
        <tbody>
            </tbody>
    </table>
    
    <div id="paginationContainer" class="pagination">
        </div>

    <script>
        // ATENÇÃO: Mudança para forçar o caminho completo, incluindo o servidor,
        // pois o caminho relativo e o caminho absoluto com o nome do projeto estão falhando devido ao FORWARD do Servlet.
        const API_URL = 'http://localhost:8080/LojaWebProject/produtos'; 
        
        const formProduto = document.getElementById('formProduto');
        const tabelaBody = document.querySelector('#tabelaProdutos tbody');
        const statusDiv = document.getElementById('status');
        const mensagemDiv = document.getElementById('mensagem');
        const formTitle = document.getElementById('formTitle');
        const btnSalvar = document.getElementById('btnSalvar');
        const btnCancelar = document.getElementById('btnCancelar');
        const paginationContainer = document.getElementById('paginationContainer');
        
        // Estado atual da paginação e filtro
        let currentPage = 1;
        let currentFilter = '';
        const limitPerPage = 10; // Definindo o limite padrão para o frontend

        // =================================================================
        // AUXILIARES DE UI
        // =================================================================
        function exibirMensagem(texto, tipo) {
            mensagemDiv.textContent = texto;
            mensagemDiv.className = `mensagem ${tipo}`; 
            mensagemDiv.style.display = 'block';
            setTimeout(() => { mensagemDiv.style.display = 'none'; }, 4000);
        }

        function resetForm() {
            formProduto.reset();
            document.getElementById('idProduto').value = '0'; // ID 0 = Inserção
            formTitle.textContent = 'Adicionar Novo';
            btnSalvar.textContent = 'Salvar Produto';
            btnCancelar.style.display = 'none';
        }
        
        btnCancelar.onclick = resetForm;
        
        // Função Debounce para evitar múltiplas requisições enquanto o usuário digita
        let timeoutFiltro;
        function debouncedCarregarProdutos() {
            clearTimeout(timeoutFiltro);
            timeoutFiltro = setTimeout(() => {
                const novoFiltro = document.getElementById('inputFiltro').value.trim();
                // Se o filtro mudou, voltamos para a primeira página
                if (novoFiltro !== currentFilter) {
                    currentFilter = novoFiltro;
                    carregarProdutos(1, currentFilter); // Recarrega na primeira página com o novo filtro
                }
            }, 500); // Espera 500ms após a última tecla
        }

        // =================================================================
        // PAGINAÇÃO: Desenha os botões de navegação
        // =================================================================
        function renderizarPaginacao(totalPaginas, paginaAtual) {
            paginationContainer.innerHTML = ''; // Limpa botões antigos
            
            // Se houver apenas 1 página ou menos, não mostra a paginação
            if (totalPaginas <= 1) return;

            // Botão ANTERIOR
            const btnAnterior = document.createElement('button');
            btnAnterior.textContent = 'Anterior';
            btnAnterior.disabled = paginaAtual === 1;
            btnAnterior.onclick = () => carregarProdutos(paginaAtual - 1, currentFilter);
            paginationContainer.appendChild(btnAnterior);

            // Geração dos botões numerados
            const range = 2; // Mostrar 2 páginas antes e 2 depois da atual
            let start = Math.max(1, paginaAtual - range);
            let end = Math.min(totalPaginas, paginaAtual + range);

            // Adiciona a primeira página e reticências se necessário
            if (start > 1) {
                paginationContainer.appendChild(createPageButton(1, paginaAtual));
                if (start > 2) {
                    paginationContainer.appendChild(createEllipsis());
                }
            }

            // Botões do miolo
            for (let i = start; i <= end; i++) {
                paginationContainer.appendChild(createPageButton(i, paginaAtual));
            }

            // Adiciona reticências e a última página se necessário
            if (end < totalPaginas) {
                if (end < totalPaginas - 1) {
                    paginationContainer.appendChild(createEllipsis());
                }
                paginationContainer.appendChild(createPageButton(totalPaginas, paginaAtual));
            }

            // Botão PRÓXIMO
            const btnProximo = document.createElement('button');
            btnProximo.textContent = 'Próximo';
            btnProximo.disabled = paginaAtual === totalPaginas;
            btnProximo.onclick = () => carregarProdutos(paginaAtual + 1, currentFilter);
            paginationContainer.appendChild(btnProximo);
        }
        
        // Auxiliar para criar um botão de página
        function createPageButton(pageNumber, paginaAtual) {
            const btn = document.createElement('button');
            btn.textContent = pageNumber;
            if (pageNumber === paginaAtual) {
                btn.classList.add('active');
            }
            btn.onclick = () => carregarProdutos(pageNumber, currentFilter);
            return btn;
        }
        
        // Auxiliar para criar reticências
        function createEllipsis() {
            const span = document.createElement('span');
            span.textContent = '...';
            span.style.padding = '0 10px';
            span.style.color = '#777';
            return span;
        }

        // =================================================================
        // CRUD: 1. LISTAR (GET) - ATUALIZADO COM PAGINAÇÃO/FILTRO
        // =================================================================
        function carregarProdutos(page = currentPage, filtro = currentFilter) {
            currentPage = page; // Atualiza a página atual globalmente
            currentFilter = filtro; // Atualiza o filtro globalmente
            
            statusDiv.innerText = `Carregando página ${currentPage}...`;
            
            // Monta a URL com os parâmetros
            const url = `${API_URL}?page=${currentPage}&limit=${limitPerPage}&filtro=${filtro}`;
            
            console.log("Tentando acessar URL:", url); // Log para depuração

            fetch(url, { method: 'GET' }) 
                .then(response => {
                    if (!response.ok) {
                        // Se a resposta não for 200 OK, exibe o erro HTTP
                        throw new Error(`Erro HTTP: ${response.status} - ${response.statusText}`);
                    }
                    // Tenta ler o JSON. Se a resposta for HTML, o JSON.parse vai falhar aqui.
                    return response.json(); 
                })
                .then(data => {
                    tabelaBody.innerHTML = ""; 
                    
                    if (data.status !== 'ok') {
                        // Trata a resposta JSON que indica erro no backend (DAO/DB)
                        throw new Error(data.mensagem || "Erro desconhecido na resposta do servidor.");
                    }
                    
                    const produtos = data.produtos;
                    const totalRegistros = data.totalRegistros;
                    const totalPaginas = data.totalPaginas;
                    
                    if(!produtos || produtos.length === 0) {
                        statusDiv.innerText = `Nenhum produto encontrado. Total de Registros: ${totalRegistros}`;
                        renderizarPaginacao(0, 1);
                        return;
                    }

                    let htmlCompleto = "";
                    
                    produtos.forEach(p => {
                        let precoNumerico = p.preco ? p.preco : 0;
                        let precoFormatado = parseFloat(precoNumerico).toFixed(2).replace('.', ',');
                        
                        htmlCompleto += `
                            <tr>
                                <td>\${p.idProduto}</td>
                                <td>\${p.nome}</td>
                                <td>\${p.marca}</td>
                                <td>\${p.modelo || '-'}</td>
                                <td>\${p.tamanho}</td>
                                <td class="preco">R$ \${precoFormatado}</td>
                                <td>\${p.estoqueMinimo || 0}</td> 
                                <td>\${p.ativo === '1' ? 'Sim' : 'Não'}</td>
                                <td>
                                    <button class="btn btn-edit" onclick="preencherFormulario(
                                        \${p.idProduto}, 
                                        '\${p.nome}', 
                                        '\${p.marca}', 
                                        '\${p.modelo}', 
                                        '\${p.tamanho}', 
                                        \${p.preco}, 
                                        \${p.estoqueMinimo}, 
                                        '\${p.ativo}' 
                                    )">Editar</button>
                                    <button class="btn btn-delete" onclick="deletarProduto(\${p.idProduto})">Excluir</button>
                                </td>
                            </tr>
                        `;
                    });

                    tabelaBody.innerHTML = htmlCompleto;
                    statusDiv.innerText = `Mostrando ${produtos.length} de ${totalRegistros} itens. Página ${currentPage} de ${totalPaginas}.`;
                    
                    // Renderiza os botões de paginação
                    renderizarPaginacao(totalPaginas, currentPage);

                })
                .catch(error => {
                    console.error('Erro ao listar produtos:', error);
                    let erroMsg = error.message;
                    // Melhorando a mensagem de erro para este caso específico
                    if (erroMsg.includes("invalid JSON") || erroMsg.includes("Unexpected token '<'")) {
                        erroMsg = "Erro de Mapeamento: O servidor retornou uma página HTML em vez de JSON. Verifique a URL na aba Network.";
                    } else if (erroMsg.includes("Erro HTTP: 500")) {
                        erroMsg = "Erro Interno do Servidor (500). Verifique o console do Tomcat/Eclipse para detalhes do SQL/Java.";
                    }
                    
                    statusDiv.innerText = "Erro ao carregar lista. Verifique o console.";
                    exibirMensagem(`ERRO: ${erroMsg}`, 'error');
                    renderizarPaginacao(0, 1);
                });
        }
        
        // =================================================================
        // CRUD: 2 & 3. INSERIR (POST) / ATUALIZAR (PUT)
        // =================================================================
        formProduto.addEventListener('submit', async function(event) {
            event.preventDefault();
            
            const idProduto = document.getElementById('idProduto').value;
            const isUpdate = idProduto !== '0';
            
            // Garante que o ID do produto seja 0 para POST e o valor real para PUT
            const produtoIdToSend = isUpdate ? parseInt(idProduto) : 0;
            
            const produtoData = {
                idProduto: produtoIdToSend, 
                nome: document.getElementById('nome').value,
                marca: document.getElementById('marca').value,
                modelo: document.getElementById('modelo').value,
                tamanho: document.getElementById('tamanho').value,
                // O servidor espera o preço como String para o BigDecimal
                preco: parseFloat(document.getElementById('preco').value).toFixed(2), 
                estoqueMinimo: parseInt(document.getElementById('estoqueMinimo').value), 
                ativo: document.getElementById('ativo').value 
            };
            
            const method = isUpdate ? 'PUT' : 'POST';
            
            try {
                const response = await fetch(API_URL, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(produtoData)
                });
                
                const result = await response.json();
                
                if (result.status === 'ok') {
                    exibirMensagem(result.mensagem || (isUpdate ? 'Produto atualizado!' : 'Produto criado!'), 'success');
                    resetForm();
                    // Após inserir ou atualizar, recarrega a lista
                    carregarProdutos(currentPage, currentFilter); 
                } else {
                    exibirMensagem(result.mensagem || 'Erro desconhecido na operação.', 'error');
                }
                
            } catch (error) {
                console.error(`Erro ao ${isUpdate ? 'atualizar' : 'inserir'} produto:`, error);
                exibirMensagem(`Erro de conexão/servidor ao ${isUpdate ? 'atualizar' : 'inserir'} produto.`, 'error');
            }
        });

        // =================================================================
        // CRUD: 4. DELETAR (DELETE)
        // =================================================================
        async function deletarProduto(id) {
            if (!confirm(`Confirma a exclusão do produto ID ${id}?`)) {
                return;
            }

            try {
                const response = await fetch(API_URL, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ idProduto: id }) 
                });
                
                const result = await response.json();
                
                if (result.status === 'ok') {
                    exibirMensagem(`Produto ID ${id} excluído com sucesso!`, 'success');
                    // Recarrega a página atual após a exclusão
                    carregarProdutos(currentPage, currentFilter); 
                } else {
                    exibirMensagem('Erro ao excluir: ' + (result.mensagem || 'Erro desconhecido'), 'error');
                }

            } catch (error) {
                console.error('Erro ao deletar produto:', error);
                exibirMensagem('Erro de conexão ao deletar produto.', 'error');
            }
        }

        // =================================================================
        // AUXILIAR: PREENCHE FORMULÁRIO PARA EDIÇÃO
        // =================================================================
        function preencherFormulario(id, nome, marca, modelo, tamanho, preco, estoqueMinimo, ativo) {
            document.getElementById('idProduto').value = id;
            document.getElementById('nome').value = nome;
            document.getElementById('marca').value = marca;
            document.getElementById('modelo').value = modelo;
            document.getElementById('tamanho').value = tamanho;
            document.getElementById('preco').value = parseFloat(preco).toFixed(2); 
            document.getElementById('estoqueMinimo').value = estoqueMinimo; 
            document.getElementById('ativo').value = ativo;
            
            formTitle.textContent = 'Editar';
            btnSalvar.textContent = 'Salvar Alterações';
            btnCancelar.style.display = 'inline-block';
            window.scrollTo(0, 0); 
        }

        // Função para Sair (Logout)
        function fazerLogout() {
            if(confirm("Deseja realmente sair?")) {
                fetch('${pageContext.request.contextPath}/auth', { 
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: 'acao=logout' 
                })
                .then(() => {
                    window.location.href = '${pageContext.request.contextPath}/home';
                });
            }
        }
        
        // Chamada inicial (inicia o carregamento da página 1)
        window.onload = () => carregarProdutos(1, '');
    </script>

</body>
</html>