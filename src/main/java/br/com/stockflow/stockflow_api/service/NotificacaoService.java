package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.response.MenuBadgeResponse;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.ProdutoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.stereotype.Service;

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

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuario == null) {

            return List.of();

        }

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
}