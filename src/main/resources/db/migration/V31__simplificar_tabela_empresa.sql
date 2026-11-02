ALTER TABLE empresa
DROP COLUMN nome_fantasia;

ALTER TABLE empresa
DROP COLUMN razao_social;

ALTER TABLE empresa
DROP COLUMN cnpj;

ALTER TABLE empresa
DROP COLUMN telefone;

ALTER TABLE empresa
DROP COLUMN email;

ALTER TABLE empresa
DROP COLUMN endereco;

ALTER TABLE empresa
DROP COLUMN cidade;

ALTER TABLE empresa
DROP COLUMN uf;

ALTER TABLE empresa
DROP COLUMN proprietario;

ALTER TABLE empresa
DROP COLUMN logo_url;

ALTER TABLE empresa
DROP COLUMN cor_primaria;

ALTER TABLE empresa
DROP COLUMN cor_secundaria;

ALTER TABLE empresa
    ALTER COLUMN tenant_id SET NOT NULL;

ALTER TABLE empresa
    ADD CONSTRAINT uk_empresa_tenant
        UNIQUE (tenant_id);