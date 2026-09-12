package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.PromocaoRequest;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.entity.Promocao;
import br.com.stockflow.stockflow_api.entity.Usuario;

import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.repository.PromocaoRepository;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final ProdutoRepository produtoRepository;

    public PromocaoService(
            PromocaoRepository promocaoRepository,
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.promocaoRepository =
                promocaoRepository;

        this.produtoRepository =
                produtoRepository;

        this.usuarioAutenticadoService =
                usuarioAutenticadoService;

    }

    public List<Promocao> listar() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        return promocaoRepository
                .findByTenantAndAtivaTrue(
                        usuarioLogado.getTenant()
                );
    }

    public List<Promocao> listarAtivas() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        return promocaoRepository
                .findByTenantAndAtivaTrue(
                        usuarioLogado.getTenant()
                );
    }

    public Promocao buscarPorId(
            UUID id) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Promocao promocao =
                promocaoRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Promoção não encontrada."
                                )
                        );

        if (!promocao
                .getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId())) {

            throw new RuntimeException(
                    "Você não possui acesso a esta promoção."
            );
        }

        return promocao;
    }

    public Promocao criarPromocao(
            PromocaoRequest request) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        Produto produto =
                produtoRepository
                        .findById(
                                request.getProdutoId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado."
                                )
                        );

        if (!produto
                .getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId()
                )) {

            throw new RuntimeException(
                    "Você não possui acesso a este produto."
            );
        }

        if (Boolean.FALSE.equals(
                produto.getAtivo())) {

            throw new RuntimeException(
                    "Não é possível criar promoção para produto inativo."
            );
        }


        Promocao promocao =
                new Promocao();

        Integer estoqueInicio =
                produto.getEstoqueAtual();

        Integer metaUnidades =
                (int) Math.ceil(
                        estoqueInicio * 0.5
                );

        promocao.setEstoqueInicio(
                estoqueInicio
        );

        promocao.setMetaUnidades(
                metaUnidades
        );

        promocao.setUnidadesVendidas(
                0
        );

        promocao.setReceitaGerada(
                BigDecimal.ZERO
        );

        promocao.setProduto(
                produto);

        promocao.setPrecoOriginal(
                produto.getPrecoVenda());

        promocao.setPrecoPromocional(
                request.getPrecoPromocional());

        promocao.setPercentualDesconto(
                request.getPercentualDesconto());

        promocao.setMotivo(
                request.getMotivo());

        promocao.setDataInicio(
                LocalDateTime.now());

        promocao.setDataCriacao(
                LocalDateTime.now());

        promocao.setAtiva(true);

        promocao.setTenant(
                usuarioLogado.getTenant());

        produto.setPromocaoAtiva(
                true);

        produto.setPrecoPromocional(
                request.getPrecoPromocional());

        produto.setPromocaoMotivo(
                request.getMotivo());

        produto.setDataInicioPromocao(
                LocalDate.now());

        produto.setDataFimPromocao(
                null);

        produtoRepository.save(
                produto);

        return promocaoRepository
                .save(promocao);


    }

    public void finalizarMetaAtingida(
            Promocao promocao,
            Produto produto) {

        System.out.println(

                "META ATINGIDA: "

                        + produto.getNome()

        );

        promocao.setAtiva(false);

        promocao.setDataFim(
                LocalDateTime.now()
        );

        produto.setPromocaoAtiva(false);

        produto.setPrecoPromocional(null);

        produto.setDataInicioPromocao(null);

        produto.setDataFimPromocao(null);

        produto.setPromocaoMotivo(
                "Meta atingida");

        produtoRepository.save(produto);

        promocaoRepository.save(promocao);
    }

    public Promocao encerrarPromocao(
            UUID id) {

        Promocao promocao =
                buscarPorId(id);

        promocao.setAtiva(false);

        promocao.setDataFim(
                LocalDateTime.now());

        Produto produto =
                promocao.getProduto();

        produto.setPromocaoAtiva(false);

        produto.setPrecoPromocional(
                null);

        produto.setPromocaoMotivo(
                null);

        produto.setDataFimPromocao(
                LocalDate.now());

        produtoRepository.save(
                produto);

        return promocaoRepository.save(
                promocao);
    }

    public List<Promocao> listarPorProduto(
            UUID produtoId) {

        Produto produto =
                produtoRepository
                        .findById(produtoId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado."
                                )
                        );

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (!produto.getTenant()
                .getId()
                .equals(
                        usuarioLogado
                                .getTenant()
                                .getId()
                )) {

            throw new RuntimeException(
                    "Você não possui acesso a este produto."
            );
        }

        return promocaoRepository
                .findByProduto(produto);
    }
}