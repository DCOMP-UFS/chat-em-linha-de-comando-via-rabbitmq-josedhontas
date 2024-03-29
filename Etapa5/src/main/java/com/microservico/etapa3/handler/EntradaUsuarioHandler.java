// Pacote com.microservico.etapa1.handler
package com.microservico.etapa3.handler;

import com.microservico.etapa3.api.RabbitApi;
import com.microservico.etapa3.chat.ChatRabbit;
import com.microservico.etapa3.thread.BaixarArquivoThread;
import com.microservico.etapa3.thread.EnviarArquivoThread;

import java.io.IOException;
import java.util.Scanner;

public class EntradaUsuarioHandler {
    private final ChatRabbit chatRabbit;
    private final Scanner scanner;
    private final RabbitApi rabbitApi;

    private String textoInput;

    public EntradaUsuarioHandler(ChatRabbit chatRabbit, RabbitApi rabbitApi, Scanner scanner) {
        this.chatRabbit = chatRabbit;
        this.scanner = scanner;
        this.rabbitApi = rabbitApi;
    }

    public void handleEntradaUsuario() {
        BaixarArquivoThread baixarArquivoThread = new BaixarArquivoThread(chatRabbit);
        baixarArquivoThread.start();
        textoInput = null;

        while (true) {
            System.out.print(textoInput != null ? textoInput + ">> " : ">> ");
            String entradaUsuario = scanner.nextLine();

            if (entradaUsuario.startsWith("!addGroup ")) {
                handleCriarGrupo(entradaUsuario);
            } else if (entradaUsuario.startsWith("!addUser")) {
                handleAdicionarUsuario(entradaUsuario);
            } else if (entradaUsuario.startsWith("!delFromGroup")) {
                handleRemoverUsuario(entradaUsuario);
            } else if (entradaUsuario.startsWith("!removeGroup")) {
                handleExcluirGrupo(entradaUsuario);
            } else if(entradaUsuario.startsWith("!upload")){
                handlerEnviarArquivo(entradaUsuario, textoInput);
            }
            else if(entradaUsuario.startsWith("!listUsers ")){
                handlerListarUsuariosGrupo(entradaUsuario);
            }
            else if(entradaUsuario.startsWith("!listGroups")){
                handlerListarGrupo(entradaUsuario);
            }
            else if (entradaUsuario.startsWith("@")) {
                handleSetDestino(entradaUsuario);
            } else if(entradaUsuario.startsWith("#")){
                handleSetGrupo(entradaUsuario);
            }
            else if (textoInput != null) {
                handleEnviarMensagem(entradaUsuario, textoInput);
            }
        }
    }

    private void handlerListarGrupo(String entradaUsuario) {
        try {
            rabbitApi.listarGruposDoUsuario(chatRabbit.getOrigem());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlerListarUsuariosGrupo(String entradaUsuario) {
        if(entradaUsuario.isEmpty()){
            return;
        }

        String nomeGrupo = entradaUsuario.substring("!listUsers ".length());
        try {
            rabbitApi.listarUsuariosDoGrupo(nomeGrupo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSetGrupo(String entradaUsuario) {
        textoInput = entradaUsuario;
    }

    private void handleSetDestino(String entradaUsuario) {
        String nomeDestino = entradaUsuario.substring(1);
        chatRabbit.setDestino(nomeDestino);
        textoInput = entradaUsuario;
    }

    private void handleCriarGrupo(String entradaUsuario) {
        String nomeGrupo = entradaUsuario.substring("!addGroup ".length());
        System.out.println(nomeGrupo);
        if(nomeGrupo.isEmpty() || nomeGrupo == null){
            return;
        }
        chatRabbit.criarGrupo(nomeGrupo);
    }

    private void handleAdicionarUsuario(String entradaUsuario) {
        String[] parametros = entradaUsuario.split(" ");
        if(parametros.length == 3){
            String nomeUsuario = parametros[1];
            String nomeGrupo = parametros[2];
            chatRabbit.adicionarUsuarioAoGrupo(nomeUsuario, nomeGrupo);
        }
        else{
            System.out.println("Formato inválido! Use: !addUser <nomeUsuario> <nomeGrupo>");
        }

    }

    private void handleRemoverUsuario(String entradaUsuario) {
        String[] parametros = entradaUsuario.split(" ");
        if(parametros.length == 3){
            String nomeUsuario = parametros[1];
            String nomeGrupo = parametros[2];
            chatRabbit.removerUsuarioDoGrupo(nomeUsuario, nomeGrupo);
        }
        else{
            System.out.println("Formato inválido! Use: !delFromGroup <nomeUsuario> <nomeGrupo>");
        }
    }

    private void handleExcluirGrupo(String entradaUsuario) {
        String nomeGrupo = entradaUsuario.substring("!removeGroup ".length());
        chatRabbit.excluirGrupo(nomeGrupo);
    }

    private void handleEnviarMensagem(String entradaUsuario, String textoInput) {
        if(entradaUsuario.isEmpty()){
            return;
        }
        char prefixo = textoInput.charAt(0);
        String nomeGrupo = textoInput.substring(1);
        if(prefixo == '@'){
            chatRabbit.enviarMensagem(entradaUsuario);
        } else{
            chatRabbit.enviarMensagem(entradaUsuario, nomeGrupo);
        }
    }

    private void handlerEnviarArquivo(String entradaUsuario, String textoInput) {
        if(entradaUsuario.isEmpty()){
            return;
        }
        char prefixo = textoInput.charAt(0);
        String nomeGrupo = textoInput.substring(1);
        String nomeArquivo = entradaUsuario.substring("!upload ".length());

        if(prefixo == '@'){;
            EnviarArquivoThread enviarArquivoThread = new EnviarArquivoThread(chatRabbit, nomeArquivo);
            enviarArquivoThread.start();
        } else{
            chatRabbit.enviarArquivo(nomeArquivo, nomeGrupo);
        }
    }
}
