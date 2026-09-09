CREATE TABLE fiado (

                       id UUID PRIMARY KEY,

                       cliente_id UUID NOT NULL,

                       venda_id UUID,

                       valor_total NUMERIC(10,2) NOT NULL,

                       data_lancamento TIMESTAMP NOT NULL,

                       status VARCHAR(50) NOT NULL,

                       observacao VARCHAR(500),

                       tenant_id UUID NOT NULL,

                       CONSTRAINT fk_fiado_cliente
                           FOREIGN KEY (cliente_id)
                               REFERENCES cliente(id),

                       CONSTRAINT fk_fiado_tenant
                           FOREIGN KEY (tenant_id)
                               REFERENCES tenant(id)

);

CREATE INDEX idx_fiado_cliente
    ON fiado(cliente_id);

CREATE INDEX idx_fiado_tenant
    ON fiado(tenant_id);