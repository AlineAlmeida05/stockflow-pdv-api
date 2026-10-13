package br.service;

import br.dto.request.ItemVendaRequest;
import br.dto.request.VendaRequest;
import br.entity.Produto;
import br.entity.Tenant;
import br.entity.Usuario;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.*;
import br.security.UsuarioAutenticadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ItemVendaRepository itemVendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private FiadoRepository fiadoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PromocaoRepository promocaoRepository;

    @Mock
    private PromocaoService promocaoService;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private VendaService vendaService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> vendaService.listar()
        );
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID produtoId = UUID.randomUUID();

        ItemVendaRequest item =
                new ItemVendaRequest(
                        produtoId,
                        1
                );

        VendaRequest request =
                new VendaRequest(
                        "dinheiro",
                        null,
                        BigDecimal.valueOf(100),
                        List.of(item)
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produtoId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> vendaService.salvar(request)
        );

    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto Teste");
        produto.setAtivo(true);
        produto.setEstoqueAtual(1);

        ItemVendaRequest item =
                new ItemVendaRequest(
                        produto.getId(),
                        10
                );

        VendaRequest request =
                new VendaRequest(
                        "dinheiro",
                        null,
                        BigDecimal.valueOf(100),
                        List.of(item)
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        assertThrows(
                RegraNegocioException.class,
                () -> vendaService.salvar(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoVendaFiadaSemCliente() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        VendaRequest request =
                new VendaRequest(
                        "fiado",
                        null,
                        null,
                        List.of()
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        assertThrows(
                RegraNegocioException.class,
                () -> vendaService.salvar(request)
        );
    }
}
