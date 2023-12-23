package com.microservico.etapa3.thread;

import com.microservico.etapa3.chat.ChatRabbit;

public class EnviarArquivoThread extends Thread {
    private final ChatRabbit chatRabbit;
    private String arquivo;

    public EnviarArquivoThread(ChatRabbit chatRabbit, String arquivo) {
        this.chatRabbit = chatRabbit;
        this.arquivo = arquivo;
        //setDaemon(true);
    }

    @Override
    public void run() {
        String nomeDestino = chatRabbit.getDestino();
        String nomeGrupo = chatRabbit.getGrupo();
        chatRabbit.enviarArquivo(arquivo);
        if (nomeGrupo.trim().isEmpty()) {
            System.out.print(!nomeDestino.isEmpty() ? "@" + nomeDestino + ">> " : ">> ");
        } else {
            System.out.print("#" + chatRabbit.getGrupo() + ">> ");
        }

    }
}

