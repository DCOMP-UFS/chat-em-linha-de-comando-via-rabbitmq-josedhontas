package com.microservico.etapa1.thread;
import com.mensagem.protobuf.MensagemBuf;
import com.microservico.etapa1.chat.ChatRabbit;

public class ReceberMensagensThread extends Thread {
    private final ChatRabbit chatRabbit;

    public ReceberMensagensThread(ChatRabbit chatRabbit) {
        this.chatRabbit = chatRabbit;
    }

    @Override
    public void run() {
        while (true) {
            String nomeDestino = chatRabbit.getDestino();
            //String nomeGrupo = chatRabbit.getGrupo();
            MensagemBuf.Mensagem mensagemRecebida = chatRabbit.receberMensagemDaFila();
            if (mensagemRecebida != null) {
                System.out.println();
                System.out.println(
                        "(" + mensagemRecebida.getData() + " às " + mensagemRecebida.getHora() + ") " +
                                mensagemRecebida.getEmissor() + " diz: " +
                                mensagemRecebida.getConteudo().getCorpo().toStringUtf8()
                );

                System.out.print(nomeDestino != null ? "@" + nomeDestino + ">> " : ">> ");
            }
        }
    }
}
