package br.service;

import br.dto.response.ProdutoResponse;
import br.entity.Produto;
import br.entity.Tenant;
import br.entity.Usuario;
import br.exception.RecursoNaoEncontradoException;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import br.dto.request.ProdutoCreateRequest;
import java.math.BigDecimal;
import static org.mockito.Mockito.verify;
import br.dto.request.ProdutoUpdateRequest;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> produtoService.listar()
        );
    }

    @Test
    void deveListarProdutosComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        List<ProdutoResponse> resultado =
                produtoService.listar();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    void deveSalvarProdutoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setCodigoTenant("EMP");

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        ProdutoCreateRequest request =
                new ProdutoCreateRequest(
                        "Produto Teste",
                        "Categoria Teste",
                        "123456789",
                        BigDecimal.valueOf(10.0),
                        50,
                        10
                );

        Produto produtoSalvo = new Produto();
        produtoSalvo.setId(UUID.randomUUID());
        produtoSalvo.setNome("Produto Teste");
        produtoSalvo.setCodigo("EMP000001");

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findTopByTenantIdOrderByCodigoDesc(
                tenant.getId()))
                .thenReturn(null);

        when(produtoRepository.save(any(Produto.class)))
                .thenReturn(produtoSalvo);

        ProdutoResponse response =
                produtoService.salvar(request);

        assertNotNull(response);
        assertEquals("Produto Teste", response.nome());
    }

    @Test
    void deveRegistrarAuditoriaAoSalvarProduto() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setCodigoTenant("EMP");

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        ProdutoCreateRequest request =
                new ProdutoCreateRequest(
                        "Produto Teste",
                        "Categoria Teste",
                        "123456789",
                        BigDecimal.valueOf(10.0),
                        50,
                        10
                );

        Produto produtoSalvo = new Produto();
        produtoSalvo.setId(UUID.randomUUID());
        produtoSalvo.setNome("Produto Teste");
        produtoSalvo.setCodigo("EMP000001");

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findTopByTenantIdOrderByCodigoDesc(
                tenant.getId()))
                .thenReturn(null);

        when(produtoRepository.save(any(Produto.class)))
                .thenReturn(produtoSalvo);

        produtoService.salvar(request);

        verify(auditoriaService).registrar(
                eq(tenant.getId()),
                eq(usuario.getId()),
                eq(usuario.getNome()),
                eq("PRODUTO_CRIADO"),
                eq("Produto"),
                eq(produtoSalvo.getId().toString()),
                contains("Produto Teste")
        );
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID produtoId = UUID.randomUUID();

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produtoId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> produtoService.buscarPorId(produtoId)
        );
    }

    @Test
    void deveInativarProdutoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTenant(tenant);

        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto Teste");
        produto.setAtivo(true);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        produtoService.excluir(produto.getId());

        assertFalse(produto.getAtivo());

        verify(produtoRepository)
                .save(produto);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        anyString(),
                        eq("PRODUTO_INATIVADO"),
                        eq("Produto"),
                        eq(produto.getId().toString()),
                        contains("inativado")
                );
    }

    @Test
    void deveReativarProdutoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto Teste");
        produto.setAtivo(false);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        produtoService.reativar(produto.getId());

        assertTrue(produto.getAtivo());

        verify(produtoRepository)
                .save(produto);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("PRODUTO_REATIVADO"),
                        eq("Produto"),
                        eq(produto.getId().toString()),
                        contains("reativado")
                );
    }

    @Test
    void deveAtualizarProdutoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        UUID produtoId = UUID.randomUUID();

        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Produto Antigo");
        produto.setAtivo(true);

        ProdutoUpdateRequest request =
                new ProdutoUpdateRequest(
                        "Produto Novo",
                        "Bebidas",
                        "123456",
                        BigDecimal.valueOf(25.00),
                        100,
                        10,
                        true
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produtoId,
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any(Produto.class)))
                .thenReturn(produto);

        ProdutoResponse response =
                produtoService.atualizar(
                        produtoId,
                        request
                );

        assertNotNull(response);

        verify(produtoRepository)
                .save(produto);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("PRODUTO_EDITADO"),
                        eq("Produto"),
                        eq(produtoId.toString()),
                        contains("atualizado")
                );
    }
}