package br.com.Alura.LiterAlura.service;

import org.springframework.stereotype.Component;

@Component
public interface IConverteDados {
    <T> T  obterDados(String json, Class<T> classe);
}
