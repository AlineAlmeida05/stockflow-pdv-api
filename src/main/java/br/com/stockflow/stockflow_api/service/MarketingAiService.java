package br.com.stockflow.stockflow_api.service;

import org.springframework.stereotype.Service;
import br.com.stockflow.stockflow_api.dto.response.MarketingAiSuggestionResponse;
import br.com.stockflow.stockflow_api.dto.response.MarketingAiResultResponse;
import java.util.List;
import java.math.BigDecimal;

@Service
public class MarketingAiService {

    public String obterContextoProduto(
            String produtoNome
    ) {

        String nome =
                produtoNome.toLowerCase();

        if (
                nome.contains("skol")
                        ||
                        nome.contains("heineken")
                        ||
                        nome.contains("brahma")
                        ||
                        nome.contains("corona")
        ) {

            return """
                    Produto do tipo cerveja.
                    Transmitir sensação de bebida gelada.
                    Utilizar gelo, gotas de água e iluminação fria.
                    """;
        }

        if (
                nome.contains("jack")
                        ||
                        nome.contains("whisky")
                        ||
                        nome.contains("red label")
                        ||
                        nome.contains("black label")
        ) {

            return """
                    Produto premium.
                    Utilizar elementos sofisticados.
                    Atmosfera elegante.
                    Madeira escura e iluminação dourada.
                    """;
        }

        if (
                nome.contains("red bull")
                        ||
                        nome.contains("monster")
        ) {

            return """
                    Produto energético.
                    Visual moderno.
                    Luzes neon.
                    Atmosfera noturna.
                    """;
        }

        return """
                Produto de adega.
                Visual comercial moderno.
                Atmosfera profissional.
                """;
    }

    public MarketingAiSuggestionResponse sugerirConfiguracao(
            String produtoNome
    ) {

        String nome =
                produtoNome.toLowerCase();

        if (
                nome.contains("skol")
                        ||
                        nome.contains("heineken")
                        ||
                        nome.contains("brahma")
                        ||
                        nome.contains("corona")
        ) {

            return new MarketingAiSuggestionResponse(
                    "Promocional",
                    "Mega Promoção",
                    "Gelo"
            );
        }

        if (
                nome.contains("jack")
                        ||
                        nome.contains("whisky")
        ) {

            return new MarketingAiSuggestionResponse(
                    "Premium",
                    "Lançamento",
                    "Madeira"
            );
        }

        return new MarketingAiSuggestionResponse(
                "Promocional",
                "Mega Promoção",
                "Automático"
        );
    }

    public MarketingAiResultResponse gerarResultado(
            String produtoNome,
            BigDecimal precoPromocional,
            String tipoPromocao,
            String tom
    ) {

        String introducao;

        switch (tom) {

            case "Urgente" ->

                    introducao =
                            "⏳ Corra, oferta por tempo limitado!";

            case "Premium" ->

                    introducao =
                            "✨ Uma oportunidade especial para quem aprecia qualidade.";

            case "Divertido" ->

                    introducao =
                            "🍻 Chame a galera e aproveite!";

            default ->

                    introducao =
                            "🔥 Não perca essa promoção!";
        }

        return new MarketingAiResultResponse(

                "%s\n\n%s\n\n%s\n\nPor apenas R$ %.2f."
                        .formatted(
                                introducao,
                                tipoPromocao,
                                produtoNome,
                                precoPromocional
                        ),

                List.of(
                        "#promocao",
                        "#adega",
                        "#oferta",
                        "#" + produtoNome
                                .split(" ")[0]
                                .toLowerCase()
                ),

                "%s\n\n%s\n\nR$ %.2f\n\n%s"
                        .formatted(
                                tipoPromocao,
                                produtoNome,
                                precoPromocional,
                                introducao
                        )
        );
    }



    public String gerarPrompt(
            String produtoNome,
            String nomeEmpresa,

            BigDecimal precoPromocional,
            String tom,
            String tipoPromocao,
            String elementoVisual,
            String contextoProduto,
            String observacoes

    ) {

        String produtoNomeAmigavel =
                produtoNome.replace(
                        "_",
                        " "
                );

        return """
            Crie uma arte publicitária profissional para divulgação de uma promoção de adega.

            Produto: %s

            Contexto do Produto:
            %s

            Empresa:
            %s

            Preço Promocional:
            R$ %s

            Tom:
            %s

            Tipo da Promoção:
            %s

            Elemento Visual:
            %s
            
            Identidade Visual da Empresa:
                
            - Utilizar a marca da empresa como elemento de destaque
            - Reservar espaço apropriado para exibição do logotipo
            - Respeitar identidade visual corporativa
            - Valorizar a marca em conjunto com o produto
            - Manter aparência profissional e comercial
            - Exibir o nome da empresa de forma clara e legível
            - Fazer com que a marca seja facilmente percebida pelo cliente
            - Não substituir a marca por textos genéricos
            
            
            Regras obrigatórias:

            - Exibir o produto como elemento principal
            - Destacar o preço promocional
            - Aparência profissional
            - Visual moderno
            - Fundo relacionado ao universo de bebidas
            - Dar destaque à marca
            - Considerar o slogan da empresa
            - Destacar visualmente a marca da empresa
            - Incluir espaço para aplicação do logotipo
            - Garantir boa legibilidade do nome da empresa
            
            Observações:

            %s

            A arte deve parecer criada por um designer profissional.
            """
                .formatted(
                        produtoNomeAmigavel,
                        contextoProduto,
                        nomeEmpresa,
                        precoPromocional,
                        tom,
                        tipoPromocao,
                        elementoVisual,
                        observacoes
                );
    }

}