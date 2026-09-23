package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.repository.MarketingCampaignRepository;
import org.springframework.stereotype.Service;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.service.EmpresaService;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import br.com.stockflow.stockflow_api.dto.request.MarketingCampaignCreateRequest;
import br.com.stockflow.stockflow_api.dto.response.MarketingCampaignResponse;
import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.dto.response.MarketingAiResultResponse;
import br.com.stockflow.stockflow_api.entity.MarketingCampaign;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketingCampaignService {

    private final MarketingCampaignRepository marketingCampaignRepository;
    private final MarketingAiService marketingAiService;
    private final ProdutoRepository produtoRepository;
    private final EmpresaService empresaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public MarketingCampaignService(
            MarketingCampaignRepository marketingCampaignRepository,
            MarketingAiService marketingAiService,
            ProdutoRepository produtoRepository,
            EmpresaService empresaService,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {

        this.marketingCampaignRepository = marketingCampaignRepository;

        this.marketingAiService = marketingAiService;

        this.produtoRepository = produtoRepository;

        this.empresaService = empresaService;

        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public MarketingCampaignResponse gerarCampanha(
            MarketingCampaignCreateRequest request
    ) {

        Produto produto =
                produtoRepository
                        .findById(
                                request.produtoId()
                        )
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Produto não encontrado."
                                        )
                        );

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        Tenant tenant =
                usuario.getTenant();

        Empresa empresa =
                empresaService
                        .obterEntidadeEmpresa();

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
                                empresa.getNomeFantasia(),
                                empresa.getSlogan(),
                                precoCampanha,
                                request.tom(),
                                request.tipoPromocao(),
                                request.elementoVisual(),
                                contextoProduto,
                                request.observacoes()
                        );

        MarketingCampaign campaign =
                new MarketingCampaign();

        campaign.setTenant(
                tenant
        );

        campaign.setProduto(
                produto
        );

        campaign.setTom(
                request.tom()
        );

        campaign.setTipoPromocao(
                request.tipoPromocao()
        );

        campaign.setElementoVisual(
                request.elementoVisual()
        );

        campaign.setContextoProduto(
                contextoProduto
        );

        campaign.setPrompt(
                prompt
        );

        campaign.setLegenda(
                resultado.legenda()
        );

        campaign.setTextoWhatsapp(
                resultado.textoWhatsapp()
        );

        campaign =
                marketingCampaignRepository
                        .save(
                                campaign
                        );

        return new MarketingCampaignResponse(

                campaign.getId(),

                produto.getNome(),

                empresa.getNomeFantasia(),

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

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        Tenant tenant =
                usuario.getTenant();

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

                                        empresaService
                                                .obterEntidadeEmpresa()
                                                .getNomeFantasia(),

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
}