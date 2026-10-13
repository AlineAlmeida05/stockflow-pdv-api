package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.MovimentacaoFinanceiraResponse;
import br.com.stockflow.stockflow_api.service.ExtratoFinanceiroService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import br.com.stockflow.stockflow_api.dto.response.IndicadoresExtratoResponse;

@RestController
@RequestMapping(
        "/api/extrato-financeiro"
)
public class ExtratoFinanceiroController {

    private final
    ExtratoFinanceiroService
            extratoFinanceiroService;

    public
    ExtratoFinanceiroController(
            ExtratoFinanceiroService
                    extratoFinanceiroService) {

        this.extratoFinanceiroService =
                extratoFinanceiroService;
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public List<MovimentacaoFinanceiraResponse>
    listarMovimentacoes() {

        return extratoFinanceiroService
                .listarMovimentacoes();
    }

    @GetMapping("/indicadores")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public IndicadoresExtratoResponse
    obterIndicadores() {

        return extratoFinanceiroService
                .obterIndicadores();

    }
}