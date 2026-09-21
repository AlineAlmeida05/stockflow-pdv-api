package br.com.stockflow.stockflow_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;

import java.util.stream.Collectors;

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
            RecursoNaoEncontradoException.class
    )
    public ResponseEntity<ApiError>
    tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.NOT_FOUND
                )
                .body(
                        new ApiError(
                                ex.getMessage()
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

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiError>
    tratarValidacao(
            MethodArgumentNotValidException ex
    ) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(
                        Collectors.joining(" | ")
                );

        return ResponseEntity
                .status(
                        HttpStatus.BAD_REQUEST
                )
                .body(
                        new ApiError(
                                mensagem
                        )
                );

    }

}
