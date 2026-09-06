CREATE TABLE produto (

    id UUID PRIMARY KEY,

    codigo_barras VARCHAR(255),

    nome VARCHAR(255) NOT NULL,

    categoria VARCHAR(255),

    preco_venda NUMERIC(10,2) NOT NULL,

    estoque_atual INTEGER NOT NULL,

    estoque_minimo INTEGER NOT NULL,

    ativo BOOLEAN NOT NULL,

    data_cadastro TIMESTAMP NOT NULL,

    data_atualizacao TIMESTAMP,

    promocao_ativa BOOLEAN,

    preco_promocional NUMERIC(10,2),

    data_inicio_promocao DATE,

    data_fim_promocao DATE,

    promocao_motivo VARCHAR(100),

    tenant_id UUID NOT NULL,

    CONSTRAINT fk_produto_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)

);

CREATE INDEX idx_produto_tenant
ON produto(tenant_id);