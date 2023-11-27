package com.microservico.etapa2.chat;
import com.google.protobuf.ByteString;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mensagem.protobuf.MensagemBuf;
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

  private String grupoNome;

  @Autowired
  public ChatRabbit(AmqpAdmin amqpAdmin) {
    this.amqpAdmin = amqpAdmin;

  }

  @Autowired
  private RabbitTemplate rabbitTemplate;

  private Queue fila(String nomeFila) {
    return new Queue(nomeFila, false, false, false);
  }

  public void enviarMensagemParaFila(String mensagem, String nomeGrupo) {
    if (nomeGrupo != null && !nomeGrupo.isEmpty()) {
      rabbitTemplate.convertAndSend(nomeGrupo, "", criarNovaMensagem(mensagem));
    } else {
      rabbitTemplate.convertAndSend(NOME_EXCHANGE, this.destinoNome, criarNovaMensagem(mensagem));
    }
  }



  private byte[] criarNovaMensagem(String mensagem2) {
    MensagemBuf.Mensagem mensagem = MensagemBuf.Mensagem.newBuilder()
            .setEmissor(getOrigem())
            .setDestino(getDestino())
            .setData(getData())
            .setHora(getHora())
            .setGrupo(getGrupo())
            .setConteudo(MensagemBuf.Conteudo.newBuilder()
                    .setTipo("aaaa")
                    .setCorpo(ByteString.copyFromUtf8(mensagem2))
                    .setNome("")
                    .build())
            .build();
    return mensagem.toByteArray();
  }

  private String getData() {
    LocalDateTime timestamp = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return timestamp.format(formatter);
  }

  private String getHora() {
    LocalDateTime timestamp = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return timestamp.format(formatter);
  }

  public MensagemBuf.Mensagem receberMensagemDaFila() {
    Message mensagem = rabbitTemplate.receive(this.origemNome);

    if (mensagem != null) {
      byte[] mensagemBytes = mensagem.getBody();

      try {
        MensagemBuf.Mensagem mensagemRecebida = MensagemBuf.Mensagem.parseFrom(mensagemBytes);
        return mensagemRecebida;
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
    }
    return null;
  }


  private DirectExchange trocaDireta() {
    return new DirectExchange(NOME_EXCHANGE);
  }

  private Binding relacionamento(Queue fila, DirectExchange troca) {
    return new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
  }

  private Binding relacionamentoGrupo(Queue fila, FanoutExchange troca) {
    Binding binding = BindingBuilder.bind(fila).to(troca);
    //System.out.println("Associação de fila à troca (fanout): " + binding.toString());
    return binding;
  }


  @PostConstruct public void incializacao(){
    System.out.println("Chat iniciado...");

  }

  public void setDestino(String destinoNome){
    this.destinoNome = destinoNome;
    //this.grupoNome = null;
    this.criaFila(destinoNome);
  }

  public String getDestino(){
    return this.destinoNome != null ? this.destinoNome : "";
  }

  public String getOrigem(){
    return this.origemNome;
  }

  public String getGrupo() {
    return this.grupoNome != null ? this.grupoNome : "";
  }

  public void setOrigem(String origemNome){
    this.origemNome = origemNome;
    this.criaFila(origemNome);
  }

  public void criarGrupo(String nomeGrupo) {
    this.grupoNome = nomeGrupo;
    this.amqpAdmin.declareExchange(new FanoutExchange(nomeGrupo));
    this.adicionarUsuarioAoGrupo(getOrigem(), nomeGrupo);
  }


  public void adicionarUsuarioAoGrupo(String usuario, String nomeGrupo) {
    Queue filaUsuario = this.fila(usuario);
    FanoutExchange trocaGrupo = new FanoutExchange(nomeGrupo);
    Binding ligacao = this.relacionamentoGrupo(filaUsuario, trocaGrupo);

    // Verifica se a fila já existe
    this.amqpAdmin.declareQueue(filaUsuario);
    // Verifica se o exchange já existe
    this.amqpAdmin.declareExchange(trocaGrupo);
    this.amqpAdmin.declareBinding(ligacao);
  }



  public void removerUsuarioDoGrupo(String usuario, String nomeGrupo) {
    this.amqpAdmin.removeBinding(this.relacionamentoGrupo(this.fila(usuario), new FanoutExchange(nomeGrupo)));
  }


  public void excluirGrupo(String nomeGrupo) {
    this.amqpAdmin.deleteExchange(nomeGrupo);
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
