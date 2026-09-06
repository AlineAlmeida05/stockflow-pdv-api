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

                return produtoRepository
                                .findById(id)
                                .orElseThrow();

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

                return produtoRepository
                                .save(produto);

        }

        public Produto atualizar(
                        UUID id,
                        Produto produtoAtualizado) {

                Produto produto = produtoRepository
                                .findById(id)
                                .orElseThrow();

                produto.setNome(
                                produtoAtualizado.getNome());

                produto.setCategoria(
                                produtoAtualizado.getCategoria());

                produto.setCodigoBarras(
                                produtoAtualizado.getCodigoBarras());

                produto.setPrecoVenda(
                                produtoAtualizado.getPrecoVenda());

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

                produtoRepository.deleteById(
                                id);

        }

        private String gerarCodigoProduto(
                        Usuario usuarioLogado) {

                String prefixo = usuarioLogado
                                .getTenant()
                                .getCodigoTenant();

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
                                        .replace(prefixo, "");

                        sequencia = Integer.parseInt(numero)
                                        + 1;

                }

                return String.format(
                                "%s%06d",
                                prefixo,
                                sequencia);

        }
}