package br.com.stockflow.stockflow_api.exception;

public class RegraNegocioException

        extends RuntimeException {

    public RegraNegocioException(
            String mensagem) {

        super(mensagem);

    }

}
