package com.microservico.etapa1.chat;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ChatRabbit {
  private static final String NOME_EXCHANGE = "amq.direct";

  private final AmqpAdmin amqpAdmin;

  private String origemNome;

  private String destinoNome;

  @Autowired
  public ChatRabbit(AmqpAdmin amqpAdmin) {
    this.amqpAdmin = amqpAdmin;

  }

  @Autowired
  private RabbitTemplate rabbitTemplate;

  private Queue fila(String nomeFila) {
    return new Queue(nomeFila, false, false, false);
  }

  public void enviarMensagemParaFila(String mensagem) {
    String mensagemFormatada = String.format("(%s) %s diz: %s", getCurrentTimestamp(), this.origemNome, mensagem);
    rabbitTemplate.convertAndSend(NOME_EXCHANGE, this.destinoNome, mensagemFormatada);
  }


  private String getCurrentTimestamp() {
    LocalDateTime timestamp = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    return timestamp.format(formatter);
  }

  public String receberMensagemDaFila() {
    Message mensagem = rabbitTemplate.receive(this.origemNome);

    if (mensagem != null) {
      String mensagemRecebida = new String(mensagem.getBody());
      System.out.println(mensagemRecebida);
      return mensagemRecebida;
    } else {
      return null;
    }
  }

  private DirectExchange trocaDireta() {
    return new DirectExchange(NOME_EXCHANGE);
  }

  private Binding relacionamento(Queue fila, DirectExchange troca) {
    return new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
  }

  @PostConstruct public void nada(){
    System.out.println("Chat iniciado...");

  }

  public void setDestino(String destinoNome){
    this.destinoNome = destinoNome;
    this.criaFila(destinoNome);
  }

  public String getDestino(){
    return this.destinoNome;
  }

  public void setOrigem(String origemNome){
    this.origemNome = origemNome;
    this.criaFila(origemNome);
  }

  private void criaFila(String nome){
    Queue fila = this.fila(nome);
    DirectExchange troca = this.trocaDireta();
    Binding ligacao = this.relacionamento(fila, troca);

    // Criando as filas no rabbitmq
    this.amqpAdmin.declareQueue(fila);
    this.amqpAdmin.declareExchange(troca);
    this.amqpAdmin.declareBinding(ligacao);

  }

}
