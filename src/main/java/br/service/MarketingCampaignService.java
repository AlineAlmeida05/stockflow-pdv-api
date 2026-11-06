package br.service;

import br.exception.RegraNegocioException;
import br.repository.MarketingCampaignRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import br.dto.request.MarketingCampaignCreateRequest;
import br.dto.response.MarketingCampaignResponse;
import br.entity.Produto;
import br.exception.RecursoNaoEncontradoException;
import br.entity.Usuario;
import br.entity.Tenant;
import br.dto.response.MarketingAiResultResponse;
import br.entity.MarketingCampaign;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketingCampaignService {

    private final MarketingCampaignRepository marketingCampaignRepository;
    private final MarketingAiService marketingAiService;
    private final ProdutoRepository produtoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public MarketingCampaignService(
            MarketingCampaignRepository marketingCampaignRepository,
            MarketingAiService marketingAiService,
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {

        this.marketingCampaignRepository = marketingCampaignRepository;
        this.marketingAiService = marketingAiService;
        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public MarketingCampaignResponse gerarCampanha(
            MarketingCampaignCreateRequest request
    ) {

        Produto produto =
                produtoRepository
                        .findByIdAndTenantId(
                                request.produtoId(),
                                usuarioAutenticadoService
                                        .tenantIdAtual()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Produto não encontrado."
                                        )
                        );

        validarProdutoElegivel(
                produto
        );

        Tenant tenant =
                usuarioAutenticadoService
                        .tenantAtual();

        BigDecimal precoCampanha =
                produto.getPrecoPromocional() != null
                        ? produto.getPrecoPromocional()
                        : produto.getPrecoVenda();

        String contextoProduto =
                marketingAiService
                        .obterContextoProduto(
                                produto.getNome()
                        );

        MarketingAiResultResponse resultado =
                marketingAiService
                        .gerarResultado(
                                produto.getNome(),
                                precoCampanha,
                                request.tipoPromocao(),
                                request.tom()
                        );

        String prompt =
                marketingAiService
                        .gerarPrompt(
                                produto.getNome(),
                                tenant.getNome(),
                                precoCampanha,
                                request.tom(),
                                request.tipoPromocao(),
                                request.elementoVisual(),
                                contextoProduto,
                                request.observacoes()
                        );

        MarketingCampaign campaign = new MarketingCampaign();

        campaign.setTenant(tenant);

        campaign.setProduto(produto);

        campaign.setTom(request.tom());

        campaign.setTipoPromocao(request.tipoPromocao());

        campaign.setElementoVisual(request.elementoVisual());

        campaign.setContextoProduto(contextoProduto);

        campaign.setPrompt(prompt);

        campaign.setLegenda(resultado.legenda());

        campaign.setTextoWhatsapp(resultado.textoWhatsapp());

        campaign = marketingCampaignRepository
                        .save(
                                campaign
                        );

        return new MarketingCampaignResponse(

                campaign.getId(),

                produto.getNome(),

                tenant.getNome(),

                campaign.getTom(),

                campaign.getTipoPromocao(),

                campaign.getElementoVisual(),

                campaign.getContextoProduto(),

                campaign.getPrompt(),

                campaign.getLegenda(),

                campaign.getTextoWhatsapp(),

                campaign.getCriadoEm()

        );

    }

    public List<MarketingCampaignResponse> listar() {

        Tenant tenant =
                usuarioAutenticadoService
                        .tenantAtual();

        return marketingCampaignRepository
                .findByTenantOrderByCriadoEmDesc(
                        tenant
                )
                .stream()
                .map(
                        campaign ->
                                new MarketingCampaignResponse(

                                        campaign.getId(),

                                        campaign.getProduto()
                                                .getNome(),

                                        campaign.getTenant()
                                                .getNome(),

                                        campaign.getTom(),

                                        campaign.getTipoPromocao(),

                                        campaign.getElementoVisual(),

                                        campaign.getContextoProduto(),

                                        campaign.getPrompt(),

                                        campaign.getLegenda(),

                                        campaign.getTextoWhatsapp(),

                                        campaign.getCriadoEm()

                                )
                )
                .collect(
                        Collectors.toList()
                );

    }

    private void validarProdutoElegivel(
            Produto produto
    ) {

        if (!Boolean.TRUE.equals(
                produto.getPromocaoAtiva()
        )) {

            throw new RegraNegocioException(
                    "Produto não possui promoção ativa."
            );

        }

    }
}