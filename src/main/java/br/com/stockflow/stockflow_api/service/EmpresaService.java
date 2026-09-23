package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.EmpresaCreateRequest;
import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import br.com.stockflow.stockflow_api.dto.response.EmpresaResponse;
import br.com.stockflow.stockflow_api.dto.request.EmpresaUpdateRequest;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import br.com.stockflow.stockflow_api.entity.Usuario;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;


@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {

        this.empresaRepository = empresaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;

    }

    public List<EmpresaResponse> listarTodas() {

        validarSuperAdmin();

        return empresaRepository.findAll()
                .stream()
                .map(this::montarResponse)
                .toList();
    }

    public EmpresaResponse salvar(
            EmpresaCreateRequest request) {

        validarSuperAdmin();

        Empresa empresa = new Empresa();

        empresa.setNomeFantasia(request.nomeFantasia());
        empresa.setRazaoSocial(request.razaoSocial());
        empresa.setCnpj(request.cnpj());
        empresa.setTelefone(request.telefone());
        empresa.setEmail(request.email());
        empresa.setEndereco(request.endereco());
        empresa.setCidade(request.cidade());
        empresa.setUf(request.uf());
        empresa.setProprietario(request.proprietario());
        empresa.setLogoUrl(request.logoUrl());
        empresa.setSlogan(request.slogan());
        empresa.setCorPrimaria(request.corPrimaria());
        empresa.setCorSecundaria(request.corSecundaria());

        return montarResponse(
                empresaRepository.save(
                        empresa
                )
        );
    }

    public EmpresaResponse obterEmpresa() {

        Empresa empresa =
                obterEmpresaDoUsuarioLogado();

        return montarResponse(
                empresa
        );

    }

    public EmpresaResponse atualizarEmpresa(
            EmpresaUpdateRequest request
    ) {

        Empresa empresa =
                obterEmpresaDoUsuarioLogado();

        empresa.setNomeFantasia(
                request.nomeFantasia()
        );

        empresa.setRazaoSocial(
                request.razaoSocial()
        );

        empresa.setTelefone(
                request.telefone()
        );

        empresa.setEmail(
                request.email()
        );

        empresa.setEndereco(
                request.endereco()
        );

        empresa.setCidade(
                request.cidade()
        );

        empresa.setUf(
                request.uf()
        );

        empresa.setProprietario(
                request.proprietario()
        );

        empresa.setLogoUrl(
                request.logoUrl()
        );

        empresa.setSlogan(
                request.slogan()
        );

        empresa.setCorPrimaria(
                request.corPrimaria()
        );

        empresa.setCorSecundaria(
                request.corSecundaria()
        );

        empresaRepository.save(
                empresa
        );

        return montarResponse(
                empresa
        );
    }

    public Empresa obterEntidadeEmpresa() {

        return obterEmpresaDoUsuarioLogado();

    }

    private Empresa obterEmpresaDoUsuarioLogado() {

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuario == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );

        }

        return empresaRepository
                .findByTenantId(
                        usuario.getTenant().getId()
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Empresa não encontrada."
                        )
                );

    }

    private EmpresaResponse montarResponse(
            Empresa empresa
    ) {

        return new EmpresaResponse(

                empresa.getId(),

                empresa.getNomeFantasia(),
                empresa.getRazaoSocial(),
                empresa.getCnpj(),

                empresa.getTelefone(),
                empresa.getEmail(),

                empresa.getEndereco(),
                empresa.getCidade(),
                empresa.getUf(),

                empresa.getProprietario(),

                empresa.getLogoUrl(),

                empresa.getSlogan(),

                empresa.getCorPrimaria(),
                empresa.getCorSecundaria(),

                empresa.getVersaoSistema(),

                empresa.getDataImplantacao()
        );
    }

    private void validarSuperAdmin() {

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (
                usuario == null
                        ||
                        usuario.getPerfil()
                                != Perfil.SUPER_ADMIN
        ) {

            throw new RegraNegocioException(
                    "Acesso permitido apenas para SUPER_ADMIN."
            );

        }

    }


}