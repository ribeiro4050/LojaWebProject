package br.com.lojaroupa.model;

public class Funcionario {
    private int idFuncionario;
    private String email; // No banco é 'email', no antigo era 'login'
    private String senha;
    private int idNivelUsuario;
    private String nome; // No banco é 'nome', no antigo era 'nomeCompleto'
    private String cpf;
    private String endereco;
    private String cep;
    private String telefone;
    private String ativo;

    public Funcionario() {}

    // Getters e Setters
    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public int getIdNivelUsuario() { return idNivelUsuario; }
    public void setIdNivelUsuario(int idNivelUsuario) { this.idNivelUsuario = idNivelUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getAtivo() { return ativo; }
    public void setAtivo(String ativo) { this.ativo = ativo; }
}