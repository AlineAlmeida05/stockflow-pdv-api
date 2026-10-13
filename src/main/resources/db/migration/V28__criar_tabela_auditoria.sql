CREATE TABLE auditoria (

                           id UUID PRIMARY KEY,

                           tenant_id UUID,

                           usuario_id UUID,

                           usuario_nome VARCHAR(255),

                           acao VARCHAR(100) NOT NULL,

                           entidade VARCHAR(100) NOT NULL,

                           entidade_id VARCHAR(255),

                           descricao VARCHAR(1000),

                           data_hora TIMESTAMP NOT NULL

);