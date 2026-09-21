package br.com.stockflow.stockflow_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            RegraNegocioException.class
    )
    public ResponseEntity<ApiError>
    tratarRegraNegocio(
            RegraNegocioException ex
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.BAD_REQUEST
                )
                .body(
                        new ApiError(
                                ex.getMessage()
                        )
                );

    }

    @ExceptionHandler(
            ResponseStatusException.class
    )
    public ResponseEntity<ApiError>
    tratarResponseStatusException(
            ResponseStatusException ex
    ) {

        return ResponseEntity
                .status(
                        ex.getStatusCode()
                )
                .body(
                        new ApiError(
                                ex.getReason()
                        )
                );

    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiError>
    tratarException(
            Exception ex
    ) {

        ex.printStackTrace();

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        new ApiError(
                                "Erro interno do servidor."
                        )
                );

    }

}
