package br.com.semalo.parceiro.client;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.semalo.parceiro.modal.ApiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;

public class cnpjaClient {

    private HttpURLConnection getHttpURLConnection(String cnpj) throws Exception {

        String BASE_URL = "https://api.cnpja.com/office/";

        // 🌐 Prepara a URL com os parâmetros desejados
        HttpURLConnection conn = (HttpURLConnection)
                new URL(BASE_URL + cnpj + "?simples=true&registrations=ORIGIN&geocoding=true&maxAge=20")
                        .openConnection();

        conn.setRequestMethod("GET");
        conn.setDoOutput(false);

        // ⏱️ Define tempo máximo de espera para não travar o Sankhya
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        String token = buscarTokenApi();

        // 🔑 Adiciona o token de autorização no cabeçalho
        conn.setRequestProperty("Authorization", token);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        return conn;
    }

    private static StringBuilder getStringBuilder(int responseCode, HttpURLConnection conn) throws IOException {
        // 📥 Verifica se lê o fluxo de sucesso ou de erro com base no código HTTP
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();

        if (is != null) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
        }
        return response;
    }

    private String buscarTokenApi() throws Exception {

        // 🗄️ Pega a conexão atual do Sankhya
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwf.getJdbcWrapper();

        NativeSql ns = new NativeSql(jdbc);
        ResultSet rs = null;

        try {
            ns.appendSql("SELECT CHAVE FROM AD_KEYSAPI WHERE CODCHAVE = 2");

            rs = ns.executeQuery();

            if (!rs.next()) {
                throw new Exception("Chave da API não encontrada na AD_KEYSAPI.");
            }

            return rs.getString("CHAVE");

        } finally {
            // 🧹 Fechamos apenas o ResultSet para libertar memória
            if (rs != null) {
                try { rs.close(); } catch (Exception ignored) {}
            }
        }
    }

    public ApiResponse send(String cnpj) {

        try {
            // 🧹 Limpa a formatação do CNPJ, mantendo apenas números
            cnpj = cnpj.replaceAll("\\D", "");

            HttpURLConnection conn = getHttpURLConnection(cnpj);

            int responseCode = conn.getResponseCode();
            StringBuilder response = getStringBuilder(responseCode, conn);

            conn.disconnect();

            return new ApiResponse(responseCode, response.toString());

        } catch (java.net.SocketTimeoutException e) {
            return new ApiResponse(408, "Timeout ao chamar API de CNPJ");

        } catch (java.net.UnknownHostException e) {
            return new ApiResponse(503, "Sem conexão com a internet ou DNS inválido");

        } catch (Exception e) {
            return new ApiResponse(500, "Erro de comunicação com API: " + e.getMessage());
        }
    }
}