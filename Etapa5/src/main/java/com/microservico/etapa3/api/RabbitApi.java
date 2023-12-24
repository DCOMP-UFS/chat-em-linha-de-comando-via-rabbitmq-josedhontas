package com.microservico.etapa3.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

public class RabbitApi {

    private String rabbitMQApiUrl;
    private String usuario;
    private String senha;

    public RabbitApi() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));

        this.rabbitMQApiUrl = properties.getProperty("rabbitmq.api.url");
        this.usuario = properties.getProperty("spring.rabbitmq.username");
        this.senha = properties.getProperty("spring.rabbitmq.password");
    }


    public static void main(String[] args) throws IOException {
    }



    public void listarGruposDoUsuario(String nomeUsuario) throws IOException {
        String url = rabbitMQApiUrl + "api/queues/%2F/" + nomeUsuario + "/bindings";
        String json = fazerRequisicaoGETComAutenticacao(url);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        for (JsonNode node : jsonNode) {
            String sourceValue = node.get("source").asText();
            if(!sourceValue.isEmpty() && !sourceValue.equals("amq.direct")){
                System.out.print( sourceValue + ", ");
                System.out.println();
            }
        }
    }

    public void listarUsuariosDoGrupo(String nomeGrupo) throws IOException {
        String url = rabbitMQApiUrl + "api/exchanges/%2F/" + nomeGrupo + "/bindings/source";
        String json = fazerRequisicaoGETComAutenticacao(url);
        //System.out.println(json);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        for (JsonNode node : jsonNode) {
            String destinationValue = node.get("destination").asText();
            if (!destinationValue.isEmpty() && !destinationValue.contains("Arquivo")) {
                System.out.print(destinationValue + ", ");
            }
        }
        System.out.println();
    }
    private String fazerRequisicaoGETComAutenticacao(String url) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) new URL(url).openConnection();
        conexao.setRequestMethod("GET");

        String credenciais = usuario + ":" + senha;
        String credenciaisCodificadas = Base64.getEncoder().encodeToString(credenciais.getBytes());
        conexao.setRequestProperty("Authorization", "Basic " + credenciaisCodificadas);

        int respostaCode = conexao.getResponseCode();
        if (respostaCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(conexao.getInputStream());
            StringBuilder resposta = new StringBuilder();
            while (scanner.hasNextLine()) {
                resposta.append(scanner.nextLine());
            }
            scanner.close();
            return resposta.toString();
        } else {
            throw new IOException("Falha na requisição GET. Código de resposta: " + respostaCode);
        }
    }
}
