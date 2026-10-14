CREATE TABLE refresh_tokens
(
    id UUID PRIMARY KEY,

    token VARCHAR(255)
        NOT NULL
        UNIQUE,

    expiracao TIMESTAMP
        NOT NULL,

    usuario_id UUID
        NOT NULL,

    CONSTRAINT fk_refresh_token_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id)
);
