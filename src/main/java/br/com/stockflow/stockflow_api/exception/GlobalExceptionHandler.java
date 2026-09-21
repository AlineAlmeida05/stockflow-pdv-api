package br.com.stockflow.stockflow_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
