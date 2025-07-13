package br.com.Alura.LiterAlura.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResultadoBusca(
            @JsonAlias("results") List<LivroDTO> resultados // Mapeia o array results
    ) {}

