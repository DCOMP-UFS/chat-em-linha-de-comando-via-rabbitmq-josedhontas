package com.microservico.etapa3.chat;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.*;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mensagem.protobuf.MensagemBuf;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
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
    return new Queue(nomeFila, true, false, false);
  }

  public void enviarMensagem(String mensagem) {
    setGrupoNome("");
    rabbitTemplate.convertAndSend(NOME_EXCHANGE, this.destinoNome, criarNovaMensagem(mensagem, "texto"));
  }


  public void enviarMensagem(String mensagem, String nomeGrupo) {
    setGrupoNome(nomeGrupo);
    criarGrupo(nomeGrupo); // para evitar o erro ao mandar mensagem para grupo inexistente
    rabbitTemplate.convertAndSend(nomeGrupo, "", criarNovaMensagem(mensagem, "texto"));
  }

  @Async
  public void enviarArquivo(String arquivo) {
    String destino = "@" + getDestino();
    setGrupoNome("");
    System.out.println();
    System.out.println("Enviando "+arquivo+ " para "+ destino + ".");
    System.out.print("@"+getDestino()+">> ");
    rabbitTemplate.convertAndSend(NOME_EXCHANGE, this.destinoNome + "Arquivo", criarNovaMensagem(arquivo, "arquivo"));
    System.out.println();
    System.out.println("Arquivo "+arquivo+ " foi enviado para " + destino+ " !");
  }

  @Async
  public void enviarArquivo(String arquivo, String grupoNome) {
    String destino = "@" + getDestino();
    setGrupoNome("");
    System.out.println();
    System.out.println("Enviando "+arquivo+ " para "+ destino + ".");
    System.out.print("@"+getDestino()+">> ");
    rabbitTemplate.convertAndSend(grupoNome, "", criarNovaMensagem(arquivo, "arquivo"));
    System.out.println();
    System.out.println("Arquivo "+arquivo+ " foi enviado para " + destino+ " !");
  }

  public byte[] criarNovaMensagem(String conteudo, String tipoConteudo) {
    MensagemBuf.Mensagem.Builder mensagemBuilder = MensagemBuf.Mensagem.newBuilder()
            .setEmissor(getOrigem())
            .setData(getData())
            .setHora(getHora())
            .setGrupo(getGrupo());

    MensagemBuf.Conteudo.Builder conteudoBuilder = MensagemBuf.Conteudo.newBuilder();

    try {
      if ("texto".equals(tipoConteudo)) {
        conteudoBuilder.setTipo("texto")
                .setCorpo(ByteString.copyFromUtf8(conteudo))
                .setNome("");
      } else if ("arquivo".equals(tipoConteudo)) {
        String filePath = URLDecoder.decode(conteudo, "UTF-8");

        Path source = Paths.get(filePath);
        String tipoMime = Files.probeContentType(source);
        byte[] corpoArquivo = Files.readAllBytes(source);
        String nomeArquivo = source.getFileName().toString(); // Obtém o nome do arquivo

        conteudoBuilder.setTipo(tipoMime)
                .setCorpo(ByteString.copyFrom(corpoArquivo))
                .setNome(nomeArquivo); // Adiciona o nome do arquivo ao conteúdo
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    mensagemBuilder.setConteudo(conteudoBuilder.build());

    return mensagemBuilder.build().toByteArray();
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
    Message mensagem = rabbitTemplate.receive(getOrigem());
    if (mensagem != null) {
      byte[] mensagemBytes = mensagem.getBody();

      try {
        //System.out.println("Mensagem recebida");
        MensagemBuf.Mensagem mensagemRecebida = MensagemBuf.Mensagem.parseFrom(mensagemBytes);
        return mensagemRecebida;
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
    }
    return null;
  }


  public MensagemBuf.Mensagem receberArquivoDaFila() {
    Message mensagem = rabbitTemplate.receive(getOrigem()+"Arquivo");
    if (mensagem != null) {
      byte[] mensagemBytes = mensagem.getBody();

      try {
        //System.out.println("Mensagem recebida");
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
    this.criaFila(destinoNome);
    this.criaFila(destinoNome+"Arquivo");

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
    this.criaFila(origemNome+"Arquivo");
  }


  public void setGrupoNome(String nomeGrupo){
    this.grupoNome = nomeGrupo;
  }

  public void criarGrupo(String nomeGrupo) {
    setGrupoNome(nomeGrupo);
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
