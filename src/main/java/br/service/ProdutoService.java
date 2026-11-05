package br.service;

import br.dto.request.ProdutoCreateRequest;
import br.dto.request.ProdutoUpdateRequest;
import br.dto.response.ProdutoResponse;
import br.entity.Produto;
import br.entity.Tenant;
import br.entity.Usuario;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final AuditoriaService auditoriaService;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            AuditoriaService auditoriaService) {

        this.produtoRepository = produtoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.auditoriaService = auditoriaService;
    }

    public List<ProdutoResponse> listar() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        return produtoRepository
                .findByTenantId(
                        tenantId)
                .stream()
                .map(this::montarResponse)
                .toList();

    }

    public ProdutoResponse buscarPorId(
            UUID id) {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Produto produto =
                produtoRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        return montarResponse(
                produto
        );

    }

    public ProdutoResponse salvar(
            ProdutoCreateRequest request) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Produto produto = new Produto();

        produto.setNome(
                request.nome());

        produto.setCategoria(
                request.categoria());

        produto.setCodigoBarras(
                request.codigoBarras());

        produto.setPrecoVenda(
                request.precoVenda());

        produto.setEstoqueAtual(
                request.estoqueAtual());

        produto.setEstoqueMinimo(
                request.estoqueMinimo());

        produto.setAtivo(true);

        Usuario usuarioLogado = obterUsuarioLogado();

        produto.setTenant(
                usuarioAutenticadoService
                        .tenantAtual());

        produto.setCodigo(
                gerarCodigoProduto()
        );

        produto.setDataCadastro(
                LocalDateTime.now());

        produto.setCustoMedio(
                request.custoMedio()
        );

        Produto produtoSalvo = produtoRepository.save(produto);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "PRODUTO_CRIADO",
                "Produto",
                produtoSalvo.getId().toString(),
                "Produto " + produtoSalvo.getNome() + " criado."
        );

        return montarResponse(produtoSalvo);

    }

    public ProdutoResponse atualizar(
            UUID id,
            ProdutoUpdateRequest request) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Produto produto =
                produtoRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        produto.setNome(
                request
                        .nome());

        produto.setCategoria(
                request
                        .categoria());

        produto.setCodigoBarras(
                request
                        .codigoBarras());

        produto.setPrecoVenda(
                request
                        .precoVenda());

        produto.setCustoMedio(
                request.custoMedio()
        );

        produto.setEstoqueAtual(
                request
                        .estoqueAtual());

        produto.setEstoqueMinimo(
                request
                        .estoqueMinimo());

        produto.setAtivo(
                request.ativo());

        produto.setDataAtualizacao(
                LocalDateTime.now());

        Produto produtoAtualizado = produtoRepository.save(produto);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "PRODUTO_EDITADO",
                "Produto",
                produtoAtualizado.getId().toString(),
                "Produto " + produtoAtualizado.getNome() + " atualizado."
        );

        return montarResponse(produtoAtualizado);

    }

    public void excluir(
            UUID id) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Produto produto =
                produtoRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        produto.setAtivo(false);

        produto.setDataAtualizacao(LocalDateTime.now());

        produtoRepository.save(produto);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "PRODUTO_INATIVADO",
                "Produto",
                produto.getId().toString(),
                "Produto " + produto.getNome() + " inativado."
        );

    }

    public void reativar(
            UUID id) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Produto produto =
                produtoRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        produto.setAtivo(true);

        produto.setDataAtualizacao(LocalDateTime.now());

        produtoRepository.save(produto);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "PRODUTO_REATIVADO",
                "Produto",
                produto.getId().toString(),
                "Produto " + produto.getNome() + " reativado."
        );


    }

    private String gerarCodigoProduto() {

        String prefixo =
                usuarioAutenticadoService
                        .tenantAtual()
                        .getCodigoTenant();

        if (prefixo == null
                || prefixo.isBlank()) {

            throw new RegraNegocioException(
                    "Tenant sem código configurado.");

        }

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Produto ultimoProduto =
                produtoRepository
                        .findTopByTenantIdOrderByCodigoDesc(
                                tenantId
                        );

        int sequencia = 1;

        if (ultimoProduto != null
                &&
                ultimoProduto.getCodigo() != null) {

            String numero = ultimoProduto
                    .getCodigo()
                    .replace(
                            prefixo,
                            "");

            sequencia = Integer.parseInt(numero)
                    + 1;

        }

        return String.format(
                "%s%06d",
                prefixo,
                sequencia);

    }

    private ProdutoResponse montarResponse(
            Produto produto
    ) {

        return new ProdutoResponse(

                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getCodigoBarras(),
                produto.getCustoMedio(),
                produto.getPrecoVenda(),
                produto.getEstoqueAtual(),
                produto.getEstoqueMinimo(),
                produto.getAtivo(),
                produto.getPromocaoAtiva(),
                produto.getPrecoPromocional()

        );

    }

    private Usuario obterUsuarioLogado() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }

}
