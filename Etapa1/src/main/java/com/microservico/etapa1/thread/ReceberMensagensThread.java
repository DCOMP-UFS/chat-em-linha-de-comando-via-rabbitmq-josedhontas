package com.microservico.etapa1.thread;

import com.microservico.etapa1.chat.ChatRabbit;

public class ReceberMensagensThread extends Thread {
    private final ChatRabbit chatRabbit;

    public ReceberMensagensThread(ChatRabbit chatRabbit) {
        this.chatRabbit = chatRabbit;
    }

    @Override
    public void run() {
        while (true) {
            String mensagemRecebida = chatRabbit.receberMensagemDaFila();
            String nomeDestino = chatRabbit.getDestino();
            if (mensagemRecebida != null) {
                System.out.println(mensagemRecebida);
                System.out.print(nomeDestino != null ? "@" + nomeDestino + ">> " : ">> ");
            }
        }
    }
}
