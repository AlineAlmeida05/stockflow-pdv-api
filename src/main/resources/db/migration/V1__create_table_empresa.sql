CREATE TABLE empresa (
    id BIGSERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(255),
    razao_social VARCHAR(255),
    cnpj VARCHAR(20),
    telefone VARCHAR(20),
    email VARCHAR(255),
    endereco VARCHAR(255),
    cidade VARCHAR(100),
    uf VARCHAR(2),
    proprietario VARCHAR(255),
    logo_url VARCHAR(500),
    slogan VARCHAR(255),
    cor_primaria VARCHAR(20),
    cor_secundaria VARCHAR(20),
    versao_sistema VARCHAR(50),
    data_implantacao DATE
);