ALTER TABLE pagamento
    ADD COLUMN usuario_id UUID;

UPDATE pagamento
SET usuario_id = (
    SELECT id
    FROM usuario
             LIMIT 1
    );

ALTER TABLE pagamento
    ALTER COLUMN usuario_id SET NOT NULL;

ALTER TABLE pagamento
    ADD CONSTRAINT fk_pagamento_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario(id);