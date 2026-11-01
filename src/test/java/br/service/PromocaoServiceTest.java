package br.service;

import br.dto.request.PromocaoRequest;
import br.entity.Promocao;
import br.repository.MovimentacaoEstoqueRepository;
import br.repository.ProdutoRepository;
import br.repository.PromocaoRepository;
import br.security.UsuarioAutenticadoService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import br.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import br.entity.Produto;
import br.entity.Tenant;
import br.entity.Usuario;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PromocaoServiceTest {

    @Mock
    private PromocaoRepository promocaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PromocaoService promocaoService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> promocaoService.listar()
        );
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID produtoId = UUID.randomUUID();

        PromocaoRequest request =
                new PromocaoRequest(produtoId);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produtoId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> promocaoService.criarPromocao(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoProdutoInativo() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setAtivo(false);
        produto.setEstoqueAtual(100);
        produto.setPrecoVenda(BigDecimal.valueOf(100));
        produto.setPromocaoAtiva(false);

        PromocaoRequest request =
                new PromocaoRequest(produto.getId());

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(promocaoRepository.findByProdutoAndAtivaTrue(produto))
                .thenReturn(Optional.empty());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        assertThrows(
                RegraNegocioException.class,
                () -> promocaoService.criarPromocao(request)
        );
    }

    @Test
    void deveCriarPromocaoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setAtivo(true);
        produto.setEstoqueAtual(100);
        produto.setPrecoVenda(BigDecimal.valueOf(100));
        produto.setPromocaoAtiva(false);

        Promocao promocao = new Promocao();
        promocao.setId(UUID.randomUUID());

        PromocaoRequest request =
                new PromocaoRequest(produto.getId());

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(promocaoRepository.findByProdutoAndAtivaTrue(produto))
                .thenReturn(Optional.empty());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        when(promocaoRepository.save(any(Promocao.class)))
                .thenReturn(promocao);

        Promocao resultado =
                promocaoService.criarPromocao(request);

        assertNotNull(resultado);

        verify(promocaoRepository)
                .save(any(Promocao.class));
    }

    @Test
    void deveRegistrarAuditoriaAoCriarPromocao() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setAtivo(true);
        produto.setEstoqueAtual(100);
        produto.setPrecoVenda(BigDecimal.valueOf(100));

        Promocao promocao = new Promocao();
        promocao.setId(UUID.randomUUID());
        promocao.setPercentualDesconto(BigDecimal.TEN);

        PromocaoRequest request =
                new PromocaoRequest(produto.getId());

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(promocaoRepository.findByProdutoAndAtivaTrue(produto))
                .thenReturn(Optional.empty());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        when(promocaoRepository.save(any(Promocao.class)))
                .thenReturn(promocao);

        promocaoService.criarPromocao(request);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("PROMOCAO_CRIADA"),
                        eq("Promocao"),
                        eq(promocao.getId().toString()),
                        contains("Produto")
                );
    }

    @Test
    void deveLancarExcecaoQuandoJaExistePromocaoAtivaParaProduto() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setAtivo(true);
        produto.setEstoqueAtual(100);
        produto.setPrecoVenda(BigDecimal.valueOf(100));

        Promocao promocaoExistente = new Promocao();

        PromocaoRequest request =
                new PromocaoRequest(produto.getId());

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(promocaoRepository.findByProdutoAndAtivaTrue(
                produto))
                .thenReturn(Optional.of(promocaoExistente));

        assertThrows(
                RegraNegocioException.class,
                () -> promocaoService.criarPromocao(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoPromocaoNaoEncontrada() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID promocaoId = UUID.randomUUID();

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(promocaoRepository.findByIdAndTenantId(
                promocaoId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RegraNegocioException.class,
                () -> promocaoService.buscarPorId(promocaoId)
        );
    }

    @Test
    void deveEncerrarPromocaoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setPromocaoAtiva(true);

        Promocao promocao = new Promocao();
        promocao.setId(UUID.randomUUID());
        promocao.setAtiva(true);
        promocao.setProduto(produto);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(promocaoRepository.findByIdAndTenantId(
                promocao.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(promocao));

        when(promocaoRepository.save(any(Promocao.class)))
                .thenReturn(promocao);

        Promocao resultado =
                promocaoService.encerrarPromocao(
                        promocao.getId()
                );

        assertNotNull(resultado);

        verify(produtoRepository)
                .save(produto);

        verify(promocaoRepository)
                .save(promocao);
    }

    @Test
    void deveRegistrarAuditoriaAoEncerrarPromocao() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setPromocaoAtiva(true);

        Promocao promocao = new Promocao();
        promocao.setId(UUID.randomUUID());
        promocao.setAtiva(true);
        promocao.setProduto(produto);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(promocaoRepository.findByIdAndTenantId(
                promocao.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(promocao));

        when(promocaoRepository.save(any(Promocao.class)))
                .thenReturn(promocao);

        promocaoService.encerrarPromocao(
                promocao.getId()
        );

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("PROMOCAO_ENCERRADA"),
                        eq("Promocao"),
                        eq(promocao.getId().toString()),
                        contains(produto.getNome())
                );
    }

    @Test
    void deveListarPromocoesAtivas() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Promocao promocao = new Promocao();

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(promocaoRepository.findByTenantAndAtivaTrue(
                tenant))
                .thenReturn(List.of(promocao));

        List<Promocao> resultado =
                promocaoService.listarAtivas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}