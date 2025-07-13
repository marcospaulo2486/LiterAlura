package br.com.Alura.LiterAlura.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class consumoAPI {

    public String obterDados(String endereco) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("Erro de I/O ao consumir a API: " + e.getMessage());
            throw new RuntimeException("Erro ao acessar a API: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            System.out.println("A requisição à API foi interrompida: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("Requisição interrompida: " + e.getMessage(), e);
        }

        String json = response.body();
        return json;
    }
}
