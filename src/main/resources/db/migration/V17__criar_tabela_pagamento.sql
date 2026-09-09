CREATE TABLE pagamento (

                           id UUID PRIMARY KEY,

                           cliente_id UUID NOT NULL,

                           valor_pago NUMERIC(10,2) NOT NULL,

                           data_pagamento TIMESTAMP NOT NULL,

                           observacao VARCHAR(500),

                           tenant_id UUID NOT NULL,

                           CONSTRAINT fk_pagamento_cliente
                               FOREIGN KEY (cliente_id)
                                   REFERENCES cliente(id),

                           CONSTRAINT fk_pagamento_tenant
                               FOREIGN KEY (tenant_id)
                                   REFERENCES tenant(id)

);

CREATE INDEX idx_pagamento_cliente
    ON pagamento(cliente_id);

CREATE INDEX idx_pagamento_tenant
    ON pagamento(tenant_id);