package br.com.stockflow.stockflow_api.exception;

public class AcessoNegadoException
        extends RuntimeException {

    public AcessoNegadoException(
            String mensagem
    ) {

        super(mensagem);

    }

}