package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Empresa;
import br.com.stockflow.stockflow_api.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import br.com.stockflow.stockflow_api.dto.response.EmpresaResponse;
import br.com.stockflow.stockflow_api.dto.EmpresaUpdateRequest;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import br.com.stockflow.stockflow_api.entity.Usuario;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


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

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Empresa salvar(
            Empresa empresa
    ) {
        return empresaRepository.save(empresa);
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
                .orElseThrow();

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

}