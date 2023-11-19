package com.microservico.etapa1;

import com.microservico.etapa1.conections.ChatRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class Etapa1 {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Etapa1.class, args);

    ChatRabbit rabbitMQConnection = context.getBean(ChatRabbit.class);

    Scanner scanner = new Scanner(System.in);
    String mensagem;
    System.out.print("User: ");
    String nomeUsuario = scanner.nextLine();
    rabbitMQConnection.setOrigem(nomeUsuario);

    String destino = null;

    Thread threadReceberMensagens = new Thread(() -> {
      while (true) {
        String mensagemRecebida = rabbitMQConnection.receberMensagemDaFila();
        if (mensagemRecebida != null) {
          System.out.print(mensagemRecebida);
        }

      }


    });

    threadReceberMensagens.start();

    while (true) {
      rabbitMQConnection.receberMensagemDaFila();

      System.out.print(destino != null ? "@" + destino + ">> " : ">> ");

      mensagem = scanner.nextLine();

      if (mensagem.startsWith("@")) {
        destino = mensagem.substring(1);
        rabbitMQConnection.setDestino(destino);

      } else {
        if (destino != null) {
          rabbitMQConnection.enviarMensagemParaFila(mensagem);
        }
      }

      rabbitMQConnection.receberMensagemDaFila();
    }
  }
}
