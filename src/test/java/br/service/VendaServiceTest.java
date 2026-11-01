package br.service;

import br.dto.request.CancelarVendaRequest;
import br.dto.request.ItemVendaRequest;
import br.dto.request.VendaRequest;
import br.entity.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
    void deveSalvarVendaComSucesso() {

            Tenant tenant = new Tenant();
            tenant.setId(UUID.randomUUID());

            Usuario usuario = new Usuario();
            usuario.setId(UUID.randomUUID());
            usuario.setNome("Administrador");
            usuario.setTenant(tenant);

            Produto produto = new Produto();
            produto.setId(UUID.randomUUID());
            produto.setNome("Coca-Cola");
            produto.setAtivo(true);
            produto.setEstoqueAtual(100);
            produto.setPrecoVenda(BigDecimal.valueOf(10));
            produto.setPromocaoAtiva(false);

            ItemVendaRequest item =
                    new ItemVendaRequest(
                            produto.getId(),
                            2
                    );

            VendaRequest request =
                    new VendaRequest(
                            "dinheiro",
                            null,
                            BigDecimal.valueOf(50),
                            List.of(item)
                    );

            Venda vendaSalva = new Venda();
            vendaSalva.setId(UUID.randomUUID());
            vendaSalva.setValorTotal(BigDecimal.valueOf(20));

            when(usuarioAutenticadoService.usuarioLogado())
                    .thenReturn(usuario);

            when(produtoRepository.findByIdAndTenantId(
                    produto.getId(),
                    tenant.getId()))
                    .thenReturn(Optional.of(produto));

            when(vendaRepository.save(any(Venda.class)))
                    .thenReturn(vendaSalva);

            Venda resultado =
                    vendaService.salvar(request);

            assertNotNull(resultado);

            verify(vendaRepository)
                    .save(any(Venda.class));
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

    @Test
    void deveRegistrarAuditoriaAoSalvarVenda() {

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
        produto.setPrecoVenda(BigDecimal.TEN);
        produto.setPromocaoAtiva(false);

        ItemVendaRequest item =
                new ItemVendaRequest(
                        produto.getId(),
                        1
                );

        VendaRequest request =
                new VendaRequest(
                        "dinheiro",
                        null,
                        BigDecimal.valueOf(20),
                        List.of(item)
                );

        Venda vendaSalva = new Venda();
        vendaSalva.setId(UUID.randomUUID());
        vendaSalva.setValorTotal(BigDecimal.TEN);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByIdAndTenantId(
                produto.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(produto));

        when(vendaRepository.save(any(Venda.class)))
                .thenReturn(vendaSalva);

        vendaService.salvar(request);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("VENDA_CRIADA"),
                        eq("Venda"),
                        eq(vendaSalva.getId().toString()),
                        contains("Venda criada")
                );
    }

    @Test
    void deveLancarExcecaoQuandoVendaNaoEncontrada() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID vendaId = UUID.randomUUID();

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(vendaRepository.findByIdAndTenantId(
                vendaId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        CancelarVendaRequest request =
                new CancelarVendaRequest(
                        "Teste"
                );

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> vendaService.cancelarVenda(
                        vendaId,
                        request
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoVendaJaCancelada() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setStatus("cancelada");

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(vendaRepository.findByIdAndTenantId(
                venda.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(venda));

        CancelarVendaRequest request =
                new CancelarVendaRequest(
                        "Teste"
                );

        assertThrows(
                RegraNegocioException.class,
                () -> vendaService.cancelarVenda(
                        venda.getId(),
                        request
                )
        );
    }

    @Test
    void deveCancelarVendaComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setEstoqueAtual(10);

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setStatus("finalizada");
        venda.setDataVenda(
                LocalDateTime.now().minusHours(1)
        );

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(2);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(vendaRepository.findByIdAndTenantId(
                venda.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(venda));

        when(itemVendaRepository.findByVendaId(
                venda.getId()))
                .thenReturn(List.of(item));

        CancelarVendaRequest request =
                new CancelarVendaRequest(
                        "Cliente desistiu"
                );

        vendaService.cancelarVenda(
                venda.getId(),
                request
        );

        assertNotNull(venda.getDataCancelamento());

        verify(vendaRepository)
                .save(venda);

        verify(produtoRepository)
                .save(produto);
    }

    @Test
    void deveRegistrarAuditoriaAoCancelarVenda() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Produto");
        produto.setEstoqueAtual(10);

        Venda venda = new Venda();
        venda.setId(UUID.randomUUID());
        venda.setStatus("finalizada");
        venda.setDataVenda(
                LocalDateTime.now().minusHours(1)
        );

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(2);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(vendaRepository.findByIdAndTenantId(
                venda.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(venda));

        when(itemVendaRepository.findByVendaId(
                venda.getId()))
                .thenReturn(List.of(item));

        CancelarVendaRequest request =
                new CancelarVendaRequest(
                        "Cliente desistiu"
                );

        vendaService.cancelarVenda(
                venda.getId(),
                request
        );

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("VENDA_CANCELADA"),
                        eq("Venda"),
                        eq(venda.getId().toString()),
                        contains("Cliente desistiu")
                );
    }
}
