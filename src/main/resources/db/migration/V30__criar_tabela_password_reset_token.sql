CREATE TABLE password_reset_tokens
(
    id UUID PRIMARY KEY,

    token VARCHAR(255)
        NOT NULL
        UNIQUE,

    expiracao TIMESTAMP
        NOT NULL,

    utilizado BOOLEAN
        NOT NULL,

    usuario_id UUID
        NOT NULL,

    CONSTRAINT fk_password_reset_token_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id)
);