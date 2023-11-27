package com.microservico.etapa2;

import com.microservico.etapa2.chat.ChatRabbit;
import com.microservico.etapa2.handler.EntradaUsuarioHandler;
import com.microservico.etapa2.thread.ReceberMensagensThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class Etapa2 {
  public static void main(String[] args) {
    try (ConfigurableApplicationContext applicationContext = SpringApplication.run(Etapa2.class, args)) {
      ChatRabbit chatRabbit = applicationContext.getBean(ChatRabbit.class);
      Scanner scanner = new Scanner(System.in);

      System.out.print(">> ");
      String nomeUsuario = scanner.nextLine();
      chatRabbit.setOrigem(nomeUsuario);

      ReceberMensagensThread receberMensagensThread = new ReceberMensagensThread(chatRabbit);
      receberMensagensThread.start();

      EntradaUsuarioHandler entradaUsuarioHandler = new EntradaUsuarioHandler(chatRabbit, scanner);
      entradaUsuarioHandler.handleEntradaUsuario();
    }
  }
}
