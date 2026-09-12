CREATE TABLE promocao (

                          id UUID PRIMARY KEY,

                          produto_id UUID NOT NULL,

                          tenant_id UUID NOT NULL,

                          preco_original NUMERIC(10,2) NOT NULL,

                          preco_promocional NUMERIC(10,2) NOT NULL,

                          percentual_desconto NUMERIC(5,2) NOT NULL,

                          motivo VARCHAR(100) NOT NULL,

                          data_inicio TIMESTAMP NOT NULL,

                          data_fim TIMESTAMP,

                          ativa BOOLEAN NOT NULL,

                          data_criacao TIMESTAMP NOT NULL,

                          CONSTRAINT fk_promocao_produto
                              FOREIGN KEY (produto_id)
                                  REFERENCES produto(id),

                          CONSTRAINT fk_promocao_tenant
                              FOREIGN KEY (tenant_id)
                                  REFERENCES tenant(id)

);

CREATE INDEX idx_promocao_produto
    ON promocao(produto_id);

CREATE INDEX idx_promocao_tenant
    ON promocao(tenant_id);

CREATE INDEX idx_promocao_ativa
    ON promocao(ativa);