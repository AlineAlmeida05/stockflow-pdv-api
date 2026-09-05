create table usuario (

    id uuid primary key,

    nome varchar(255) not null,

    email varchar(255) not null unique,

    senha varchar(255) not null,

    ativo boolean not null,

    perfil varchar(50) not null,

    tenant_id uuid not null,

    constraint fk_usuario_tenant
        foreign key (tenant_id)
        references tenant(id)

);