package br.com.stockflow.stockflow_api.entity;

public enum Perfil {

    SUPER_ADMIN(6),

    PROPRIETARIO(5),

    SOCIO(4),

    GERENTE(3),

    OPERADOR_CAIXA(2),

    ESTOQUISTA(1);

    private final int nivel;

    Perfil(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

}