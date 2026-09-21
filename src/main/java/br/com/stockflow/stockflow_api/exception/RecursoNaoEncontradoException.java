package br.com.stockflow.stockflow_api.exception;

public class RecursoNaoEncontradoException
        extends RuntimeException {

    public RecursoNaoEncontradoException(
            String mensagem
    ) {

        super(mensagem);

    }

}