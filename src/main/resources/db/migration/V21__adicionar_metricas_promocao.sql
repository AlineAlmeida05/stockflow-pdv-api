ALTER TABLE promocao
    ADD COLUMN estoque_inicio INTEGER;

ALTER TABLE promocao
    ADD COLUMN meta_unidades INTEGER;

ALTER TABLE promocao
    ADD COLUMN unidades_vendidas INTEGER
        DEFAULT 0;

ALTER TABLE promocao
    ADD COLUMN receita_gerada NUMERIC(10,2)
        DEFAULT 0;