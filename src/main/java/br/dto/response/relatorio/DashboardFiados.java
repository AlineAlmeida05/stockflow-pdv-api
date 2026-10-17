package br.dto.response.relatorio;

import java.util.List;

public record DashboardFiados(

        ResumoFiados resumo,

        List<DevedorDTO> maioresDevedores,

        List<FiadoResumoDTO> ultimosLancamentos

) {
}