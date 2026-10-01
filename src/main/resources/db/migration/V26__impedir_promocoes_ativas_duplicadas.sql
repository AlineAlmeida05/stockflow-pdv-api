CREATE UNIQUE INDEX
    uk_promocao_ativa_produto
    ON promocao(produto_id)
    WHERE ativa = true;