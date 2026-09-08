CREATE TABLE movimentacao_estoque (

    id UUID PRIMARY KEY,

    produto_id UUID NOT NULL,

    produto_nome VARCHAR(255) NOT NULL,

    tipo VARCHAR(20) NOT NULL,

    quantidade INTEGER NOT NULL,

    preco_compra NUMERIC(10,2),

    observacao VARCHAR(500),

    usuario_id UUID NOT NULL,

    tenant_id UUID NOT NULL,

    data_movimentacao TIMESTAMP NOT NULL,

    CONSTRAINT fk_movimentacao_produto
        FOREIGN KEY (produto_id)
        REFERENCES produto(id),

    CONSTRAINT fk_movimentacao_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_movimentacao_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)

);