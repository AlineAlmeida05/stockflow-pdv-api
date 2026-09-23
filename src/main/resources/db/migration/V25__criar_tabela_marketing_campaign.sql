CREATE TABLE marketing_campaign (

                                    id UUID PRIMARY KEY,

                                    tenant_id UUID NOT NULL,

                                    produto_id UUID NOT NULL,

                                    tom VARCHAR(100),

                                    tipo_promocao VARCHAR(100),

                                    elemento_visual VARCHAR(100),

                                    contexto_produto TEXT,

                                    prompt TEXT,

                                    legenda TEXT,

                                    texto_whatsapp TEXT,

                                    criado_em TIMESTAMP NOT NULL,

                                    CONSTRAINT fk_marketing_campaign_tenant
                                        FOREIGN KEY (tenant_id)
                                            REFERENCES tenant(id),

                                    CONSTRAINT fk_marketing_campaign_produto
                                        FOREIGN KEY (produto_id)
                                            REFERENCES produto(id)

);