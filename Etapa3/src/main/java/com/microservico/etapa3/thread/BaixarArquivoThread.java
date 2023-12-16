package com.microservico.etapa3.thread;
import com.mensagem.protobuf.MensagemBuf;
import com.microservico.etapa3.chat.ChatRabbit;
import java.io.FileOutputStream;

public class BaixarArquivoThread extends Thread {
    private final ChatRabbit chatRabbit;

    public BaixarArquivoThread(ChatRabbit chatRabbit) {
        this.chatRabbit = chatRabbit;
    }

    @Override
    public void run() {
        while (true) {
            String nomeFila = chatRabbit.getOrigem() + "Arquivo";
            try {
                MensagemBuf.Mensagem mensagem = chatRabbit.receberMensagemDaFila(nomeFila);
                if (mensagem != null) {
                    String tipo = mensagem.getConteudo().getTipo();
                    if (!"texto".equals(tipo)) {
                        String conteudoArquivo = mensagem.getConteudo().getCorpo().toStringUtf8();
                        String nomeDestino = chatRabbit.getDestino();
                        String nomeGrupo = mensagem.getGrupo();
                        String nomeEmissor = mensagem.getEmissor();
                        String nomeArquivo = mensagem.getConteudo().getNome();
                        try (FileOutputStream outputStream = new FileOutputStream(nomeArquivo)) {
                            outputStream.write(mensagem.getConteudo().getCorpo().toByteArray());
                            System.out.println();
                            System.out.println(
                                    "(" + mensagem.getData() + " às " + mensagem.getHora() + ") " + "Arquivo "+ nomeArquivo+ " recebido de " +
                                            nomeEmissor +  (nomeGrupo.isEmpty() ? " !" : "#" + nomeGrupo)
                            );
                            if(nomeGrupo.trim().isEmpty()){
                                System.out.print(!nomeDestino.isEmpty() ? "@" + nomeDestino + ">> " : ">> ");
                            }
                            else {
                                System.out.print("#" + chatRabbit.getGrupo() + ">> ");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
