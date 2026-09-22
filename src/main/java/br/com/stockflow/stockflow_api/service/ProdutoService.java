package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.AcessoNegadoException;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.dto.request.ProdutoCreateRequest;
import br.com.stockflow.stockflow_api.dto.request.ProdutoUpdateRequest;
import br.com.stockflow.stockflow_api.dto.response.ProdutoResponse;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.produtoRepository = produtoRepository;

        this.usuarioAutenticadoService = usuarioAutenticadoService;

    }

    public List<ProdutoResponse> listar() {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        return produtoRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId())
                .stream()
                .map(this::montarResponse)
                .toList();


    }

    public ProdutoResponse buscarPorId(
            UUID id) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Produto produto = produtoRepository
                .findByIdAndTenantId(
                        id,
                        usuarioLogado
                                .getTenant()
                                .getId()
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

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        produto.setTenant(
                usuarioLogado.getTenant());

        produto.setCodigo(
                gerarCodigoProduto(
                        usuarioLogado));

        produto.setDataCadastro(
                LocalDateTime.now());

        produto.setCustoMedio(
                java.math.BigDecimal.ZERO);


        return montarResponse(
                produtoRepository.save(produto)
        );

    }

    public ProdutoResponse atualizar(
            UUID id,
            ProdutoUpdateRequest request) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        Produto produto = produtoRepository
                .findByIdAndTenantId(
                        id,
                        usuarioLogado
                                .getTenant()
                                .getId()
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

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

        produto.setAtivo(
                request.ativo());

        produto.setDataAtualizacao(
                LocalDateTime.now());

        return montarResponse(
                produtoRepository.save(produto)
        );

    }

    public void excluir(
            UUID id) {

        Usuario usuarioLogado =
                usuarioAutenticadoService

                        .usuarioLogado();

        Produto produto = produtoRepository
                .findByIdAndTenantId(
                        id,
                        usuarioLogado
                                .getTenant()
                                .getId()
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        produto.setAtivo(false);

        produto.setDataAtualizacao(
                LocalDateTime.now());

        produtoRepository.save(
                produto);

    }

    public void reativar(
            UUID id) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Produto produto = produtoRepository
                .findByIdAndTenantId(
                        id,
                        usuarioLogado
                                .getTenant()
                                .getId()
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado."
                        ));

        produto.setAtivo(true);

        produto.setDataAtualizacao(
                LocalDateTime.now());

        produtoRepository.save(
                produto);

    }

    private String gerarCodigoProduto(
            Usuario usuarioLogado) {

        String prefixo = usuarioLogado
                .getTenant()
                .getCodigoTenant();

        if (prefixo == null
                || prefixo.isBlank()) {

            throw new RegraNegocioException(
                    "Tenant sem código configurado.");

        }

        Produto ultimoProduto = produtoRepository
                .findTopByTenantIdOrderByCodigoDesc(
                        usuarioLogado
                                .getTenant()
                                .getId());

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

    private void validarProdutoAtivo(
            Produto produto) {

        if (Boolean.FALSE.equals(
                produto.getAtivo())) {

            throw new RegraNegocioException(
                    "Produto inativo.");

        }

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

                produto.getPrecoVenda(),

                produto.getEstoqueAtual(),

                produto.getEstoqueMinimo(),

                produto.getAtivo()

        );

    }

}
