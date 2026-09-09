create table venda (

    id uuid primary key,

    data_venda timestamp not null,

    forma_pagamento varchar(20) not null,

    valor_total numeric(10,2) not null,

    quantidade_itens integer not null,

    status varchar(20) not null,

    motivo_cancelamento varchar(255),

    data_cancelamento timestamp,

    cliente_id uuid,

    cliente_nome varchar(150),

    usuario_id uuid not null,

    tenant_id uuid not null,

    constraint fk_venda_usuario
        foreign key (usuario_id)
        references usuario(id),

    constraint fk_venda_tenant
        foreign key (tenant_id)
        references tenant(id)

);