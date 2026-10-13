package br.service;

import br.dto.request.MovimentacaoEstoqueRequest;
import br.entity.MovimentacaoEstoque;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.MovimentacaoEstoqueRepository;
import br.repository.ProdutoRepository;
import br.security.UsuarioAutenticadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import br.entity.Produto;
import br.entity.Tenant;
import br.entity.Usuario;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MovimentacaoEstoqueServiceTest {
    @Mock
    private MovimentacaoEstoqueRepository repository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private MovimentacaoEstoqueService service;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> service.listar()
        );
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID produtoId = UUID.randomUUID();

        MovimentacaoEstoqueRequest request =
                new MovimentacaoEstoqueRequest(
                        produtoId,
                        "entrada",
                        10,
                        BigDecimal.TEN,
                        "Entrada teste"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produtoId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.salvar(request)
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

        MovimentacaoEstoqueRequest request =
                new MovimentacaoEstoqueRequest(
                        produto.getId(),
                        "entrada",
                        10,
                        BigDecimal.TEN,
                        "Entrada"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        assertThrows(
                RegraNegocioException.class,
                () -> service.salvar(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoEntradaSemPrecoCompra() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setAtivo(true);

        MovimentacaoEstoqueRequest request =
                new MovimentacaoEstoqueRequest(
                        produto.getId(),
                        "entrada",
                        10,
                        null,
                        "Entrada"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        assertThrows(
                RegraNegocioException.class,
                () -> service.salvar(request)
        );
    }

    @Test
    void deveSalvarEntradaComSucesso() {

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
        produto.setEstoqueAtual(0);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(UUID.randomUUID());

        MovimentacaoEstoqueRequest request =
                new MovimentacaoEstoqueRequest(
                        produto.getId(),
                        "entrada",
                        10,
                        BigDecimal.TEN,
                        "Entrada"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(repository.save(any(MovimentacaoEstoque.class)))
                .thenReturn(movimentacao);

        MovimentacaoEstoque resultado =
                service.salvar(request);

        assertNotNull(resultado);

        verify(produtoRepository)
                .save(produto);

        verify(repository)
                .save(any(MovimentacaoEstoque.class));
    }

    @Test
    void deveRegistrarAuditoriaAoRegistrarEntrada() {

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
        produto.setEstoqueAtual(0);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

        MovimentacaoEstoqueRequest request =
                new MovimentacaoEstoqueRequest(
                        produto.getId(),
                        "entrada",
                        10,
                        BigDecimal.TEN,
                        "Entrada"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(repository.save(any(MovimentacaoEstoque.class)))
                .thenReturn(movimentacao);

        service.salvar(request);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("ENTRADA_ESTOQUE"),
                        eq("Produto"),
                        eq(produto.getId().toString()),
                        contains("Entrada")
                );
    }
}
