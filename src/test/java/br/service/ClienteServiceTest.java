package br.service;

import br.dto.request.ClienteRequest;
import br.dto.response.ClienteResponse;
import br.entity.Cliente;
import br.entity.Tenant;
import br.entity.Usuario;
import br.repository.ClienteRepository;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
import br.security.UsuarioAutenticadoService;
import br.service.AuditoriaService;
import br.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import br.exception.RecursoNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private FiadoRepository fiadoRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(null);

        assertThrows(
                ResponseStatusException.class,
                () -> clienteService.listar()
        );
    }

    @Test
    void deveListarClientesComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByTenantId(
                tenant.getId()))
                .thenReturn(List.of());

        List<ClienteResponse> resultado =
                clienteService.listar();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    void deveSalvarClienteComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        ClienteRequest request =
                new ClienteRequest(
                        "João",
                        "11999999999",
                        BigDecimal.valueOf(500),
                        "Cliente teste"
                );

        Cliente clienteSalvo = new Cliente();

        clienteSalvo.setId(UUID.randomUUID());
        clienteSalvo.setNome("João");
        clienteSalvo.setTelefone("11999999999");
        clienteSalvo.setAtivo(true);
        clienteSalvo.setLimiteCredito(
                BigDecimal.valueOf(500)
        );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(clienteSalvo);

        ClienteResponse response =
                clienteService.salvar(request);

        assertNotNull(response);
    }

    @Test
    void deveRegistrarAuditoriaAoSalvarCliente() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        ClienteRequest request =
                new ClienteRequest(
                        "João",
                        "11999999999",
                        BigDecimal.valueOf(500),
                        "Cliente teste"
                );

        Cliente clienteSalvo = new Cliente();

        clienteSalvo.setId(UUID.randomUUID());
        clienteSalvo.setNome("João");

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(clienteSalvo);

        clienteService.salvar(request);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("CLIENTE_CRIADO"),
                        eq("Cliente"),
                        eq(clienteSalvo.getId().toString()),
                        contains("João")
                );
    }

    @Test
    void deveLancarExcecaoAoBuscarClienteInexistente() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setTenant(tenant);

        UUID clienteId = UUID.randomUUID();

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                clienteId,
                tenant.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.obterResumo(clienteId)
        );
    }

    @Test
    void deveInativarClienteComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Cliente cliente = new Cliente();

        cliente.setId(UUID.randomUUID());
        cliente.setNome("João");
        cliente.setAtivo(true);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        clienteService.inativar(cliente.getId());

        assertFalse(cliente.getAtivo());

        verify(clienteRepository)
                .save(cliente);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("CLIENTE_INATIVADO"),
                        eq("Cliente"),
                        eq(cliente.getId().toString()),
                        contains("inativado")
                );
    }

    @Test
    void deveReativarClienteComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        Cliente cliente = new Cliente();

        cliente.setId(UUID.randomUUID());
        cliente.setNome("João");
        cliente.setAtivo(false);

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                cliente.getId(),
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        clienteService.reativar(cliente.getId());

        assertTrue(cliente.getAtivo());

        verify(clienteRepository)
                .save(cliente);
    }

    @Test
    void deveAtualizarClienteComSucesso() {

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Administrador");
        usuario.setTenant(tenant);

        UUID clienteId = UUID.randomUUID();

        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Antigo");

        ClienteRequest request =
                new ClienteRequest(
                        "Novo Nome",
                        "11999999999",
                        BigDecimal.valueOf(1000),
                        "Atualizado"
                );

        when(usuarioAutenticadoService.usuarioLogado())
                .thenReturn(usuario);

        when(clienteRepository.findByIdAndTenantId(
                clienteId,
                tenant.getId()))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);

        ClienteResponse response =
                clienteService.atualizar(
                        clienteId,
                        request
                );

        assertNotNull(response);

        verify(clienteRepository)
                .save(cliente);

        verify(auditoriaService)
                .registrar(
                        eq(tenant.getId()),
                        eq(usuario.getId()),
                        eq(usuario.getNome()),
                        eq("CLIENTE_EDITADO"),
                        eq("Cliente"),
                        eq(clienteId.toString()),
                        contains("atualizado")
                );
    }

}
