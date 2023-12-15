package com.microservico.etapa3.api;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RabbitApi {

    // Parâmetros predefinidos
    private static final String apiUrl = "http://localhost:15672/api/exchanges";
    private static final String username = "admin";
    private static final String password = "123456";

    public static void main(String[] args) {
        // Obter o JSON das exchanges usando a classe de serviço
        String exchangesJson = getExchangesJson();
        // Verificar se o JSON foi obtido com sucesso
        if (exchangesJson != null) {
            // Exibir os dados
            System.out.println("Lista de Exchanges: " + exchangesJson);
        }

    }

    public static JSONObject getBlidings() {
        try {
            // Criar a URL e abrir a conexão
            URL url = new URL("http://localhost:15672/api/bindings");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar a autenticação básica
            String auth = username + ":" + password;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeaderValue = "Basic " + encodedAuth;
            connection.setRequestProperty("Authorization", authHeaderValue);

            // Configurar a solicitação como GET
            connection.setRequestMethod("GET");

            // Obter a resposta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Ler a resposta
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Retornar o JSON como uma string
                return new JSONObject(response.toString());
            } else {
                System.out.println("Falha na solicitação. Código de resposta: " + responseCode);
            }

            // Fechar a conexão
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getExchangesJson() {
        try {
            // Criar a URL e abrir a conexão
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar a autenticação básica
            String auth = username + ":" + password;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String authHeaderValue = "Basic " + encodedAuth;
            connection.setRequestProperty("Authorization", authHeaderValue);

            // Configurar a solicitação como GET
            connection.setRequestMethod("GET");

            // Obter a resposta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Ler a resposta
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Retornar o JSON como uma string
                return response.toString();
            } else {
                System.out.println("Falha na solicitação. Código de resposta: " + responseCode);
            }

            // Fechar a conexão
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void Teste(){
        JSONObject obj = getBlidings();
        System.out.println(obj.length());

    }
}
