package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

        public List<Produto> listar() {

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                return produtoRepository
                                .findByTenantId(
                                                usuarioLogado
                                                                .getTenant()
                                                                .getId());

        }

        public Produto buscarPorId(
                        UUID id) {

                Produto produto = produtoRepository
                                .findById(id)
                                .orElseThrow();

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (!pertenceAoMesmoTenant(
                                usuarioLogado,
                                produto)) {

                        throw new RuntimeException(
                                        "Você não possui acesso a este produto.");

                }

                return produto;

        }

        public Produto salvar(
                        Produto produto) {

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
                if (produto.getCustoMedio() == null) {

                        produto.setCustoMedio(
                                        java.math.BigDecimal.ZERO);
                }

                return produtoRepository
                                .save(produto);

        }

        public Produto atualizar(
                        UUID id,
                        Produto produtoAtualizado) {

                Produto produto = produtoRepository
                                .findById(id)
                                .orElseThrow();

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (!pertenceAoMesmoTenant(
                                usuarioLogado,
                                produto)) {

                        throw new RuntimeException(
                                        "Você não possui permissão para editar este produto.");

                }

                produto.setNome(
                                produtoAtualizado.getNome());

                produto.setCategoria(
                                produtoAtualizado.getCategoria());

                produto.setCodigoBarras(
                                produtoAtualizado.getCodigoBarras());

                produto.setPrecoVenda(
                                produtoAtualizado.getPrecoVenda());

                produto.setEstoqueAtual(
                                produtoAtualizado.getEstoqueAtual());

                produto.setEstoqueMinimo(
                                produtoAtualizado.getEstoqueMinimo());

                produto.setAtivo(
                                produtoAtualizado.getAtivo());

                produto.setDataAtualizacao(
                                LocalDateTime.now());

                return produtoRepository
                                .save(produto);

        }

        public void excluir(
                        UUID id) {

                Produto produto = produtoRepository
                                .findById(id)
                                .orElseThrow();

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (!pertenceAoMesmoTenant(
                                usuarioLogado,
                                produto)) {

                        throw new RuntimeException(
                                        "Você não possui permissão para inativar este produto.");

                }

                produto.setAtivo(false);

                produto.setDataAtualizacao(
                                LocalDateTime.now());

                produtoRepository.save(
                                produto);

        }

        public void reativar(
                        UUID id) {

                Produto produto = produtoRepository
                                .findById(id)
                                .orElseThrow();

                Usuario usuarioLogado = usuarioAutenticadoService
                                .usuarioLogado();

                if (!pertenceAoMesmoTenant(
                                usuarioLogado,
                                produto)) {

                        throw new RuntimeException(
                                        "Você não possui permissão para reativar este produto.");

                }

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

                        throw new RuntimeException(
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

        private boolean pertenceAoMesmoTenant(
                        Usuario usuario,
                        Produto produto) {

                return usuario
                                .getTenant()
                                .getId()
                                .equals(
                                                produto
                                                                .getTenant()
                                                                .getId());

        }

        private void validarProdutoAtivo(
                        Produto produto) {

                if (Boolean.FALSE.equals(
                                produto.getAtivo())) {

                        throw new RuntimeException(
                                        "Produto inativo.");

                }

        }

}
