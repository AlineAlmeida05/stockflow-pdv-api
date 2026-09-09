package br.com.stockflow.stockflow_api.dto;

public class CancelarVendaRequest {

    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(
            String motivo) {

        this.motivo = motivo;
    }
}