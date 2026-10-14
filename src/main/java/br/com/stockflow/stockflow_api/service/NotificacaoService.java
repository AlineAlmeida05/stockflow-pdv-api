package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.MenuBadgeResponse;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificacaoService {

    private final ProdutoRepository produtoRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final PromocaoService promocaoService;

    public NotificacaoService(
            ProdutoRepository produtoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            PromocaoService promocaoService
    ){

        this.produtoRepository = produtoRepository;

        this.usuarioAutenticadoService = usuarioAutenticadoService;

        this.promocaoService = promocaoService;
    }

    public List<MenuBadgeResponse>
    obterBadgesMenu() {

        Usuario usuarioLogado =
                obterUsuarioLogado();

        int totalPromocoes =
                promocaoService
                        .listarCandidatos()
                        .size();

        return List.of(
                new MenuBadgeResponse(
                        "PROMOCOES",
                        totalPromocoes
                )
        );
    }

    private Usuario obterUsuarioLogado() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }
}