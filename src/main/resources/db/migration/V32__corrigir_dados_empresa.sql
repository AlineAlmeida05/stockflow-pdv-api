UPDATE empresa
SET versao_sistema = '1.0.0'
WHERE versao_sistema IS NULL;

UPDATE empresa
SET data_implantacao = CURRENT_DATE
WHERE data_implantacao IS NULL;