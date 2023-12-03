package com.microservico.etapa2.thread;
import com.mensagem.protobuf.MensagemBuf;
import com.microservico.etapa2.chat.ChatRabbit;

public class ReceberMensagensThread extends Thread {
    private final ChatRabbit chatRabbit;

    public ReceberMensagensThread(ChatRabbit chatRabbit) {
        this.chatRabbit = chatRabbit;
    }

    @Override
    public void run() {
        while (true) {
            MensagemBuf.Mensagem mensagemRecebida = chatRabbit.receberMensagemDaFila();
            if (mensagemRecebida != null) {
                String nomeGrupo = chatRabbit.getGrupo();
                String nomeDestino = chatRabbit.getDestino();
                System.out.println();
                System.out.println(
                        "(" + mensagemRecebida.getData() + " às " + mensagemRecebida.getHora() + ") " +
                                mensagemRecebida.getEmissor() +  (nomeGrupo.trim().isEmpty() ? "" : "#" + nomeGrupo) +" diz: " +
                                mensagemRecebida.getConteudo().getCorpo().toStringUtf8()
                );
                if(nomeGrupo.trim().isEmpty()){
                    System.out.print(nomeDestino != null ? "@" + nomeDestino + ">> " : ">> ");
                }
                else {
                    System.out.print("#" + nomeGrupo + ">> ");
                }}
        }
    }
}
