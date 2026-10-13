package br.service;

import br.dto.request.PagamentoRequest;
import br.dto.response.PagamentoResponse;
import br.entity.*;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.ClienteRepository;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import br.entity.Cliente;
import br.entity.Fiado;
import br.entity.Pagamento;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FiadoRepository fiadoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PagamentoService pagamentoService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> pagamentoService.listar()
        );
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID clienteId = UUID.randomUUID();

        PagamentoRequest request =
                new PagamentoRequest(
                        clienteId,
                        BigDecimal.valueOf(50),
                        "Pagamento teste",
                        "PIX"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                clienteId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> pagamentoService.salvar(request)
        );
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoMaiorQueSaldoDevedor() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());

        Fiado fiado = new Fiado();
        fiado.setValorTotal(BigDecimal.valueOf(100));

        PagamentoRequest request =
                new PagamentoRequest(
                        cliente.getId(),
                        BigDecimal.valueOf(150),
                        "Pagamento teste",
                        "PIX"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        when(fiadoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of(fiado));

        when(pagamentoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of());

        assertThrows(
                RegraNegocioException.class,
                () -> pagamentoService.salvar(request)
        );
    }

    @Test
    void deveSalvarPagamentoComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("João");

        Fiado fiado = new Fiado();
        fiado.setValorTotal(BigDecimal.valueOf(100));

        PagamentoRequest request =
                new PagamentoRequest(
                        cliente.getId(),
                        BigDecimal.valueOf(50),
                        "Pagamento teste",
                        "PIX"
                );

        Pagamento pagamentoSalvo = new Pagamento();
        pagamentoSalvo.setId(UUID.randomUUID());
        pagamentoSalvo.setValorPago(BigDecimal.valueOf(50));

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        when(fiadoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of(fiado));

        when(pagamentoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of());

        when(pagamentoRepository.save(any(Pagamento.class)))
                .thenReturn(pagamentoSalvo);

        Pagamento resultado =
                pagamentoService.salvar(request);

        assertNotNull(resultado);

        verify(pagamentoRepository)
                .save(any(Pagamento.class));
    }

    @Test
    void deveRegistrarAuditoriaAoSalvarPagamento() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("João");

        Fiado fiado = new Fiado();
        fiado.setValorTotal(BigDecimal.valueOf(100));

        PagamentoRequest request =
                new PagamentoRequest(
                        cliente.getId(),
                        BigDecimal.valueOf(50),
                        "Pagamento teste",
                        "PIX"
                );

        Pagamento pagamentoSalvo = new Pagamento();
        pagamentoSalvo.setId(UUID.randomUUID());
        pagamentoSalvo.setValorPago(BigDecimal.valueOf(50));

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        when(fiadoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of(fiado));

        when(pagamentoRepository.findByClienteIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(List.of());

        when(pagamentoRepository.save(any(Pagamento.class)))
                .thenReturn(pagamentoSalvo);

        pagamentoService.salvar(request);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("PAGAMENTO_REALIZADO"),
                        eq("Pagamento"),
                        eq(pagamentoSalvo.getId().toString()),
                        contains("João")
                );
    }

        @Test
        void deveListarPagamentosComSucesso() {

            Tenant tenant = new Tenant();
            tenant.setId(UUID.randomUUID());

            Usuario usuario = new Usuario();
            usuario.setTenant(tenant);

            when(usuarioAutenticadoService.usuarioLogado())
                    .thenReturn(usuario);

            when(pagamentoRepository.findByTenantId(
                    tenant.getId()))
                    .thenReturn(List.of());

            List<PagamentoResponse> resultado =
                    pagamentoService .listar();

            assertNotNull(resultado);
            assertEquals(0, resultado.size());
        }
}
