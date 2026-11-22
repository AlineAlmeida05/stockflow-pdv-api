ALTER TABLE pedido_delivery
ALTER COLUMN numero TYPE VARCHAR(20);

ALTER TABLE pedido_delivery
    ADD COLUMN sequencial BIGINT;