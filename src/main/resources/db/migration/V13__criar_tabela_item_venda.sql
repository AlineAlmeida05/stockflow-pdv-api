create table item_venda (

    id uuid primary key,

    venda_id uuid not null,

    produto_id uuid not null,

    produto_nome varchar(255) not null,

    quantidade integer not null,

    valor_unitario numeric(10,2) not null,

    subtotal numeric(10,2) not null,

    promocao_aplicada boolean not null,

    constraint fk_item_venda_venda
        foreign key (venda_id)
        references venda(id),

    constraint fk_item_venda_produto
        foreign key (produto_id)
        references produto(id)

);