## 👨‍💻 Autor

Projeto desenvolvido para integração com o ecossistema Sankhya, 
Desenvolvedor Interno Raul Lopes - Semalo Fabrica de Sabores.

# Integração CNPJ-A + Sankhya

Projeto de integração entre o ERP **Sankhya** e a API **CNPJA**, responsável por consultar dados de empresas a partir do CNPJ e realizar a persistência automática dessas informações na tabela de parceiros (`TGFPAR`).

---

## 📌 Objetivo

Automatizar o cadastro/atualização de parceiros no Sankhya consumindo dados oficiais através da API CNPJA, evitando:

- digitação manual
- inconsistência cadastral
- retrabalho operacional

---

## 🏗 Arquitetura

O projeto é composto por três camadas principais:

### 1️⃣ Action (Rotina no Sankhya)

Responsável por:

- receber o CNPJ como entrada
- chamar o client da API
- tratar os dados recebidos
- inserir/atualizar o parceiro no banco

### 2️⃣ Client HTTP (CnpjaClient)

Responsável por:

- montar a requisição HTTP
- buscar o token da API no banco (`AD_KEYSAPI`)
- consumir a API CNPJA
- retornar o JSON convertido em DTO

### 3️⃣ Persistência

Utiliza:

- `EntityFacadeFactory`
- `JdbcWrapper`
- `NativeSql`
- `Registro`

para realizar operações no banco de forma transacional e segura.

---

## 🔐 Segurança

O token da API **não fica hardcoded**.

Ele é armazenado na tabela:


Estrutura mínima esperada:

| Campo | Descrição |
|-------|-----------|
ID      | PK (valor fixo = 1)  
CHAVE   | Token da API  

---

## ⚙️ Tecnologias utilizadas

- Java
- JAPE (Sankhya)
- JDBC Wrapper
- API REST (CNPJA)
- JSON

---

## 🚀 Fluxo de execução

1. Usuário informa o CNPJ na rotina
2. Action chama o `CnpjaClient`
3. Client:
   - busca o token na `AD_KEYSAPI`
   - consome a API
4. Dados são convertidos em DTO
5. Registro é criado/atualizado na `TGFPAR`

---

## ⏱ Timeouts da API

Configurados para evitar travamentos:

```java
conn.setConnectTimeout(15000);
conn.setReadTimeout(30000);
```
## 🧪 Tratamento de erros

O projeto possui tratamento para:

- CNPJ inválido

- falha de conexão com API

- token não encontrado

- erro de SQL

- retorno vazio da API

  
## 🏢 Projeto Desenvolvido

Desenvolvido para **Semalo Indústria e Comércio de Alimentos** durante meu período como Desenvolvedor.

## 👨‍💻 Autor

- **Raul** - Desenvolvedor
