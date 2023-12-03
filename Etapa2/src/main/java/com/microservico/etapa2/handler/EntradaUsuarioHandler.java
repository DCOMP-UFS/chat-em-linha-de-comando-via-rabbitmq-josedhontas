// Pacote com.microservico.etapa1.handler
package com.microservico.etapa2.handler;

import com.microservico.etapa2.chat.ChatRabbit;

import java.util.Scanner;

public class EntradaUsuarioHandler {
    private final ChatRabbit chatRabbit;
    private final Scanner scanner;

    private String textoInput;

    public EntradaUsuarioHandler(ChatRabbit chatRabbit, Scanner scanner) {
        this.chatRabbit = chatRabbit;
        this.scanner = scanner;
    }

    public void handleEntradaUsuario() {
        textoInput = null;

        while (true) {
            System.out.print(textoInput != null ? textoInput + ">> " : ">> ");
            String entradaUsuario = scanner.nextLine();

            if (entradaUsuario.startsWith("!addGroup")) {
                handleCriarGrupo(entradaUsuario);
            } else if (entradaUsuario.startsWith("!addUser")) {
                handleAdicionarUsuario(entradaUsuario);
            } else if (entradaUsuario.startsWith("!delFromGroup")) {
                handleRemoverUsuario(entradaUsuario);
            } else if (entradaUsuario.startsWith("!removeGroup")) {
                handleExcluirGrupo(entradaUsuario);
            } else if (entradaUsuario.startsWith("@")) {
                handleSetDestino(entradaUsuario);
            } else if(entradaUsuario.startsWith("#")){
                handleSetGrupo(entradaUsuario);
            }
            else if (textoInput != null) {
                handleEnviarMensagem(entradaUsuario, textoInput);
            }
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
            chatRabbit.enviarMensagemParaFila(entradaUsuario, "");
        } else{
            chatRabbit.enviarMensagemParaFila(entradaUsuario, nomeGrupo);
        }
    }
}
