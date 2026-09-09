alter table venda
add column usuario_cancelamento_id uuid;

alter table venda
add constraint fk_venda_usuario_cancelamento
foreign key (usuario_cancelamento_id)
references usuario(id);