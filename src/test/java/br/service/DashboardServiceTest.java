package br.service;

import br.repository.FiadoRepository;
import br.repository.ItemVendaRepository;
import br.repository.MovimentacaoEstoqueRepository;
import br.repository.ProdutoRepository;
import br.repository.VendaRepository;
import br.security.UsuarioAutenticadoService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import br.dto.response.DashboardResponse;
import br.entity.Tenant;
import br.entity.Usuario;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import br.entity.Produto;
import br.entity.Venda;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private FiadoRepository fiadoRepository;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ItemVendaRepository itemVendaRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> dashboardService.obterDashboard("hoje")
        );
    }

    @Test
    void deveRetornarDashboardVazio() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        when(vendaRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        when(fiadoRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        DashboardResponse resultado =
                dashboardService.obterDashboard("hoje");

        assertNotNull(resultado);

        assertEquals(0, resultado.totalProdutos());
        assertEquals(0, resultado.totalVendas());
    }

    @Test
    void deveIgnorarVendasCanceladasNoFaturamento() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Venda venda = new Venda();
        venda.setStatus("cancelada");
        venda.setValorTotal(BigDecimal.valueOf(500));
        venda.setDataVenda(LocalDateTime.now());

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(vendaRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of(venda));

        when(fiadoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        DashboardResponse resultado =
                dashboardService.obterDashboard("hoje");

        assertEquals(
                BigDecimal.ZERO,
                resultado.faturamento()
        );
    }

    @Test
    void deveContarProdutosSemEstoque() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setEstoqueAtual(0);
        produto.setEstoqueMinimo(5);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of(produto));

        when(vendaRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(fiadoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        DashboardResponse resultado =
                dashboardService.obterDashboard("hoje");

        assertEquals(
                1,
                resultado.produtosSemEstoque()
        );
    }

    @Test
    void deveContarProdutosComEstoqueBaixo() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setEstoqueAtual(3);
        produto.setEstoqueMinimo(5);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of(produto));

        when(vendaRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(fiadoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        DashboardResponse resultado =
                dashboardService.obterDashboard("hoje");

        assertEquals(
                1,
                resultado.produtosComEstoqueBaixo()
        );
    }

    @Test
    void deveContarPromocoesAtivas() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Produto produto = new Produto();
        produto.setPromocaoAtiva(true);
        produto.setEstoqueAtual(10);
        produto.setEstoqueMinimo(5);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(produtoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of(produto));

        when(vendaRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(fiadoRepository.findByTenantId(tenant.getId()))
                .thenReturn(List.of());

        when(movimentacaoEstoqueRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        DashboardResponse resultado =
                dashboardService.obterDashboard("hoje");

        assertEquals(
                1,
                resultado.promocoesAtivas()
        );
    }
}