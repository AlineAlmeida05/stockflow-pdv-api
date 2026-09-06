ALTER TABLE produto
ADD COLUMN codigo VARCHAR(20);

CREATE INDEX idx_produto_codigo
ON produto(codigo);