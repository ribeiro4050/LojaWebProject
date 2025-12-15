package br.com.lojaroupa.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import br.com.lojaroupa.dao.ProdutoDAO;
import br.com.lojaroupa.model.Produto;

// Seu mapeamento é "/produtos"
@WebServlet("/produtos") 
public class ProdutoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private Gson gson = new Gson();

    // =================================================================
    // Método Auxiliar: Lê o corpo JSON da requisição (para POST/PUT/DELETE)
    // =================================================================
    private JsonObject getJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        if (sb.length() == 0) {
            return new JsonObject();
        }
        return gson.fromJson(sb.toString(), JsonObject.class);
    }
    
    // =================================================================
    // Método Auxiliar: Converte JsonObject para Objeto Produto
    // =================================================================
    private Produto jsonToProduto(JsonObject json) {
        Produto p = new Produto();
        
        // ID do Produto (necessário para PUT/UPDATE e para retorno)
        if (json.has("idProduto") && !json.get("idProduto").isJsonNull()) {
            p.setIdProduto(json.get("idProduto").getAsInt());
        }

        // Campos obrigatórios (assumindo que o formulário os envia)
        if (json.has("nome")) p.setNome(json.get("nome").getAsString());
        if (json.has("marca")) p.setMarca(json.get("marca").getAsString());
        if (json.has("tamanho")) p.setTamanho(json.get("tamanho").getAsString());

        // Campos opcionais/numéricos
        if (json.has("modelo") && !json.get("modelo").isJsonNull()) {
            p.setModelo(json.get("modelo").getAsString());
        }
        
        // Preço (BigDecimal)
        if (json.has("preco")) {
            // Usa String para evitar problemas de precisão com floats/doubles no JSON
            p.setPreco(new BigDecimal(json.get("preco").getAsString()));
        }
        
        // Estoque Mínimo (usado para ESTOQUE INICIAL no POST e como valor no PUT)
        if (json.has("estoqueMinimo") && !json.get("estoqueMinimo").isJsonNull()) {
            p.setEstoqueMinimo(json.get("estoqueMinimo").getAsInt());
        }
        
        // Ativo
        if (json.has("ativo")) {
            p.setAtivo(json.get("ativo").getAsString());
        }
        
        return p;
    }

    // =================================================================
    // 1. LISTAR COM PAGINAÇÃO E FILTRO (READ) - doGet() - ATUALIZADO
    // =================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject jsonRetorno = new JsonObject();
        
        // --- 1. Captura de Parâmetros de Paginação e Filtro ---
        int pagina = 1;
        int limite = 10; // Padrão: 10 produtos por página
        String filtro = request.getParameter("filtro");
        
        try {
            // Tenta obter os parâmetros da URL, se existirem
            if (request.getParameter("page") != null) {
                pagina = Integer.parseInt(request.getParameter("page"));
            }
            if (request.getParameter("limit") != null) {
                limite = Integer.parseInt(request.getParameter("limit"));
            }
            
            // Garante que a página não seja menor que 1
            if (pagina < 1) pagina = 1;
            
            // Calcula o OFFSET (Ponto de início no banco de dados)
            int offset = (pagina - 1) * limite;
            
            // --- 2. Chamadas ao DAO ---
            
            // A. Obtém a lista de produtos da página atual
            List<Produto> produtos = produtoDAO.listarComPaginacao(limite, offset, filtro);
            
            // B. Obtém o total de registros (para calcular o total de páginas)
            int totalRegistros = produtoDAO.contarTotalProdutos(filtro);
            
            // C. Calcula o total de páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);

            // --- 3. Monta a Resposta JSON ---
            
            // Converte a lista de Produtos para JsonArray
            JsonArray produtosJson = gson.toJsonTree(produtos).getAsJsonArray();
            
            jsonRetorno.addProperty("status", "ok");
            jsonRetorno.add("produtos", produtosJson);
            jsonRetorno.addProperty("totalRegistros", totalRegistros);
            jsonRetorno.addProperty("totalPaginas", totalPaginas);
            jsonRetorno.addProperty("paginaAtual", pagina);
            
            out.print(gson.toJson(jsonRetorno));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Parâmetros de paginação (page, limit) devem ser números válidos.");
            out.print(gson.toJson(jsonRetorno));
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Erro interno ao processar a listagem: " + e.getMessage());
            out.print(gson.toJson(jsonRetorno));
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }

    // =================================================================
    // 2. INSERIR (CREATE) - doPost()
    // =================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject jsonRetorno = new JsonObject();
        
        try {
            JsonObject json = getJsonBody(request);
            Produto novoProduto = jsonToProduto(json);
            
            // Validação básica
            if (novoProduto.getNome() == null || novoProduto.getNome().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonRetorno.addProperty("status", "erro");
                jsonRetorno.addProperty("mensagem", "Nome do produto é obrigatório.");
            } else {
                produtoDAO.inserir(novoProduto); 
                
                jsonRetorno.addProperty("status", "ok");
                jsonRetorno.addProperty("mensagem", "Produto criado com sucesso!");
                System.out.println("✅ Novo Produto Inserido: " + novoProduto.getNome());
            }
            
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Formato JSON inválido. Verifique os tipos de dados.");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Erro interno do servidor ao salvar o produto: " + e.getMessage());
            e.printStackTrace();
        }

        out.write(gson.toJson(jsonRetorno));
    }

    // =================================================================
    // 3. ATUALIZAR (UPDATE) - doPut()
    // =================================================================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject jsonRetorno = new JsonObject();

        try {
            JsonObject json = getJsonBody(request);
            Produto produtoAtualizar = jsonToProduto(json);
            
            if (produtoAtualizar.getIdProduto() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonRetorno.addProperty("status", "erro");
                jsonRetorno.addProperty("mensagem", "ID do produto é obrigatório para atualização.");
            } else {
                produtoDAO.atualizar(produtoAtualizar);
                
                jsonRetorno.addProperty("status", "ok");
                jsonRetorno.addProperty("mensagem", "Produto atualizado com sucesso!");
                System.out.println("✅ Produto Atualizado: " + produtoAtualizar.getIdProduto());
            }

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Formato JSON inválido.");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Erro interno do servidor ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.write(gson.toJson(jsonRetorno));
    }

    // =================================================================
    // 4. DELETAR (DELETE) - doDelete()
    // =================================================================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        JsonObject jsonRetorno = new JsonObject();

        try {
            JsonObject json = getJsonBody(request);
            int idProduto = json.get("idProduto").getAsInt();
            
            produtoDAO.deletar(idProduto);
            
            jsonRetorno.addProperty("status", "ok");
            jsonRetorno.addProperty("mensagem", "Produto deletado com sucesso!");
            System.out.println("❌ Produto Deletado: " + idProduto);

        } catch (JsonSyntaxException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "ID do produto inválido ou ausente.");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonRetorno.addProperty("status", "erro");
            jsonRetorno.addProperty("mensagem", "Erro interno do servidor ao deletar produto: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.write(gson.toJson(jsonRetorno));
    }
}