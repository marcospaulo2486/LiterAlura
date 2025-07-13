package br.com.Alura.LiterAlura.service;

import br.com.Alura.LiterAlura.model.Autor;
import br.com.Alura.LiterAlura.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    // OPCAO 3
    public List<Autor> listarTodosOsAutores() {
        return autorRepository.findAll();
    }

    // OPCAO 4
    public List<Autor> listarAutoresVivosEmAno(Integer ano) {

        return autorRepository.findAll().stream()
                .filter(a -> (a.getBirth_year() != null && a.getBirth_year() <= ano) &&
                        (a.getDeath_year() == null || a.getDeath_year() >= ano))
                .collect(Collectors.toList());
    }
}
