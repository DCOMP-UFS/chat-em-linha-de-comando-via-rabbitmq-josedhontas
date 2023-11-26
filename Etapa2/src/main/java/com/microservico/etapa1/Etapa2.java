package com.microservico.etapa1;


import com.microservico.etapa1.chat.ChatRabbit;
import com.microservico.etapa1.thread.ReceberMensagensThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class Etapa2 {
  public static void main(String[] args) {
    try (ConfigurableApplicationContext context = SpringApplication.run(Etapa2.class, args)) {
      ChatRabbit chatRabbit = context.getBean(ChatRabbit.class);
      Scanner scanner = new Scanner(System.in);

      System.out.print("User: ");
      String nomeUsuario = scanner.nextLine();
      chatRabbit.setOrigem(nomeUsuario);

      ReceberMensagensThread receberMensagensThread = new ReceberMensagensThread(chatRabbit);
      receberMensagensThread.start();

      String textoInput = null;
      while (true) {
        System.out.print(textoInput != null ?  textoInput+ ">> " : ">> ");
        String input = scanner.nextLine();

        if (input.startsWith("!addGroup")) {
          String nomeGrupo = input.substring("!addGroup ".length());
          chatRabbit.criarGrupo(nomeGrupo);
        } else if (input.startsWith("!addUser")) {
          String[] parametros = input.split(" ");
          if (parametros.length == 3) {
            nomeUsuario = parametros[1];
            String nomeGrupo = parametros[2];
            chatRabbit.adicionarUsuarioAoGrupo(nomeUsuario, nomeGrupo);
          } else {
            System.out.println("Formato inválido! Use: !addUser <nomeUsuario> <nomeGrupo>");
          }
        } else if (input.startsWith("!delFromGroup")) {
          String[] parametros = input.split(" ");
          if (parametros.length == 3) {
            nomeUsuario = parametros[1];
            String nomeGrupo = parametros[2];
            chatRabbit.removerUsuarioDoGrupo(nomeUsuario, nomeGrupo);
          } else {
            System.out.println("Formato inválido! Use: !delFromGroup <nomeUsuario> <nomeGrupo>");
          }
        } else if (input.startsWith("!removeGroup")) {
          String nomeGrupo = input.substring("!removeGroup ".length());
          chatRabbit.excluirGrupo(nomeGrupo);
        } else if (input.startsWith("#") || input.startsWith("@")) {
          textoInput = input;
          if(input.startsWith("#")){
            chatRabbit.criarGrupo(input.substring(1));
          } else{
            chatRabbit.setDestino(input.substring(1));
          }
          String nomeGrupo = input.substring(1);
          chatRabbit.setDestino(nomeGrupo);
        } else if (input != null) {
          if(textoInput.startsWith("#")){
            chatRabbit.enviarMensagemParaFila(input, textoInput.substring(1));
          }
          else{
            chatRabbit.enviarMensagemParaFila(input, "");
          }
        }
      }
    }
  }
}
