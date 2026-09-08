alter table produto
add column custo_medio numeric(10,2);

update produto
set custo_medio = 0
where custo_medio is null;

alter table produto
alter column custo_medio set not null;