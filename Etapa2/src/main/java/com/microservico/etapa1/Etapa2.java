package com.microservico.etapa1;


import com.microservico.etapa1.chat.ChatRabbit;
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
      chatRabbit.setDestino("joao");
      chatRabbit.criarGrupo("joanes");
      chatRabbit.adicionarUsuarioAoGrupo(nomeUsuario, "joanes");
      chatRabbit.enviarMensagemParaFila("oieee", "joanes");
      //chatRabbit.enviarMensagemParaFila("oiee", "");
      chatRabbit.receberMensagemDaFila();
      //chatRabbit.removerUsuarioDoGrupo(nomeUsuario, "joanes");

      //ReceberMensagensThread receberMensagensThread = new ReceberMensagensThread(chatRabbit);
      //receberMensagensThread.start();
      /*
      String destino = null;
      while (true) {
        System.out.print(destino != null ? "@" + destino + ">> " : ">> ");
        String mensagem = scanner.nextLine();

        if (mensagem.startsWith("@")) {
          destino = mensagem.substring(1);
          chatRabbit.setDestino(destino);
        } else if (destino != null) {
          chatRabbit.enviarMensagemParaFila(mensagem);
        }
      }*/
    }
  }
}
