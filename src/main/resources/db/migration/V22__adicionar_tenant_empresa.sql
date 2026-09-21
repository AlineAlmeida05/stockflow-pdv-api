ALTER TABLE empresa
    ADD COLUMN tenant_id UUID;

ALTER TABLE empresa
    ADD CONSTRAINT fk_empresa_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenant(id);