<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout: Endereço e Pagamento</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f4; }
        .checkout-container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); max-width: 600px; margin: auto; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        /* Ajuste para que inputs e selects ocupem a largura total */
        .form-group input, .form-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .btn-submit { background-color: #28a745; color: white; padding: 12px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; margin-top: 10px; }
        .btn-submit:hover { background-color: #218838; }
        /* Adicionando layout para campos lado a lado */
        .form-row { display: flex; gap: 15px; }
        .form-row .form-group { flex: 1; }
        .form-row .form-group.w-33 { flex: 0 0 32%; } /* Para campos menores como Número e CEP */
    </style>
</head>
<body>

    <div class="checkout-container">
        <h1>📦 Finalizar Compra</h1>

        <p>Preencha os dados de entrega e forma de envio para concluir o pedido.</p>

        <form method="POST" action="${pageContext.request.contextPath}/checkout">
            
            <h3>Endereço e Contato para Entrega</h3>
            
            <div class="form-row">
                <div class="form-group">
                    <label for="cep">CEP:</label>
                    <input type="text" id="cep" name="cep" placeholder="Ex: 01001-000" maxlength="9" required>
                </div>
                <div class="form-group">
                    <label for="telefone">Telefone:</label>
                    <input type="text" id="telefone" name="telefone" placeholder="(11) 98765-4321" maxlength="15" required>
                </div>
            </div>
            
            <div class="form-group">
                <label for="logradouro">Rua/Avenida:</label>
                <input type="text" id="logradouro" name="logradouro" placeholder="Rua das Flores" required>
            </div>
            
            <div class="form-row">
                <div class="form-group w-33">
                    <label for="numero">Número:</label>
                    <input type="text" id="numero" name="numero" placeholder="123" required>
                </div>
                <div class="form-group">
                    <label for="complemento">Complemento (Opcional):</label>
                    <input type="text" id="complemento" name="complemento" placeholder="Apto 401">
                </div>
            </div>
            
            <div class="form-group">
                <label for="bairro">Bairro:</label>
                <input type="text" id="bairro" name="bairro" placeholder="Centro" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="cidade">Cidade:</label>
                    <input type="text" id="cidade" name="cidade" placeholder="São Paulo" required>
                </div>
                <div class="form-group w-33">
                    <label for="estado">Estado (UF):</label>
                    <input type="text" id="estado" name="estado" placeholder="SP" maxlength="2" required>
                </div>
            </div>

            <hr>

            <h3>Forma de Envio e Pagamento</h3>
            
            <div class="form-group">
                <label for="tipoFrete">Método de Envio:</label>
                <select id="tipoFrete" name="tipoFrete" required>
                    <option value="0">Retirada na Loja (Grátis)</option>
                    <option value="1">Retirada no Correio</option>
                    <option value="2">SEDEX</option>
                    <option value="3">Motoboy</option>
                    <option value="4">Transportadora</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="formaPagamento">Método de Pagamento:</label>
                <select id="formaPagamento" name="formaPagamento" required>
                    <option value="cartao">Cartão de Crédito</option>
                    <option value="boleto">Boleto</option>
                    <option value="pix">PIX</option>
                </select>
            </div>

            <button type="submit" class="btn-submit">
                Concluir Pedido
            </button>
            
        </form>
    </div>

</body>
</html>