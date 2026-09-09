CREATE TABLE cliente (

                         id UUID PRIMARY KEY,

                         nome VARCHAR(255) NOT NULL,

                         telefone VARCHAR(50) NOT NULL,

                         ativo BOOLEAN NOT NULL DEFAULT TRUE,

                         data_cadastro TIMESTAMP NOT NULL,

                         limite_credito NUMERIC(10,2) NOT NULL,

                         observacao VARCHAR(500),

                         tenant_id UUID NOT NULL,

                         CONSTRAINT fk_cliente_tenant
                             FOREIGN KEY (tenant_id)
                                 REFERENCES tenant(id)

);

CREATE INDEX idx_cliente_tenant
    ON cliente(tenant_id);