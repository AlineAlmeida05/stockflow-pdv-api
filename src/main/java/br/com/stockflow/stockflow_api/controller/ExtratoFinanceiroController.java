package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.MovimentacaoFinanceiraResponse;
import br.com.stockflow.stockflow_api.service.ExtratoFinanceiroService;
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
    public List<
            MovimentacaoFinanceiraResponse>
    listarMovimentacoes() {

        return extratoFinanceiroService
                .listarMovimentacoes();
    }

    @GetMapping("/indicadores")
    public IndicadoresExtratoResponse
    obterIndicadores() {

        return extratoFinanceiroService
                .obterIndicadores();

    }
}