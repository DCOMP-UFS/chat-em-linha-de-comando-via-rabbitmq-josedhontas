package com.microservico.etapa3;

import com.microservico.etapa3.api.RabbitApi;
import com.microservico.etapa3.chat.ChatRabbit;
import com.microservico.etapa3.handler.EntradaUsuarioHandler;
import com.microservico.etapa3.thread.ReceberMensagensThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class Etapa5 {
  public static void main(String[] args) {
    try (ConfigurableApplicationContext applicationContext = SpringApplication.run(Etapa5.class, args)) {
      ChatRabbit chatRabbit = applicationContext.getBean(ChatRabbit.class);
      RabbitApi rabbitApi = new RabbitApi();
      Scanner scanner = new Scanner(System.in);

      System.out.print("User: ");
      String nomeUsuario = scanner.nextLine();
      chatRabbit.setOrigem(nomeUsuario);

      ReceberMensagensThread receberMensagensThread = new ReceberMensagensThread(chatRabbit);
      receberMensagensThread.start();

      EntradaUsuarioHandler entradaUsuarioHandler = new EntradaUsuarioHandler(chatRabbit, rabbitApi, scanner);
      entradaUsuarioHandler.handleEntradaUsuario();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }
}
