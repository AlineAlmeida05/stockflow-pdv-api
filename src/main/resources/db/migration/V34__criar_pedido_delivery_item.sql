CREATE TABLE pedido_delivery_item (
    id UUID PRIMARY KEY,

    pedido_id UUID NOT NULL,

    produto_id UUID NOT NULL,

    tenant_id UUID NOT NULL,

    quantidade INTEGER NOT NULL,

    preco_unitario NUMERIC(19,2) NOT NULL,

    subtotal NUMERIC(19,2) NOT NULL,

    CONSTRAINT fk_pedido_item_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedido_delivery(id),

    CONSTRAINT fk_pedido_item_produto
        FOREIGN KEY (produto_id)
        REFERENCES produto(id),

    CONSTRAINT fk_pedido_item_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)
);