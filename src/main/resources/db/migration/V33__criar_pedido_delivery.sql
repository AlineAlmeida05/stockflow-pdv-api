CREATE TABLE pedido_delivery (
    id UUID PRIMARY KEY,

    numero BIGINT NOT NULL,

    cliente_nome VARCHAR(150) NOT NULL,

    telefone VARCHAR(30) NOT NULL,

    endereco VARCHAR(255) NOT NULL,

    complemento VARCHAR(255),

    observacao VARCHAR(500),

    valor_total NUMERIC(19,2) NOT NULL,

    status VARCHAR(30) NOT NULL,

    data_pedido TIMESTAMP NOT NULL,

    data_entrega TIMESTAMP,

    tenant_id UUID NOT NULL,

    CONSTRAINT fk_pedido_delivery_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)
);