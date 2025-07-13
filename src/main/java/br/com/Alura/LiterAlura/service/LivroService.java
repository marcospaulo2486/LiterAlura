package br.com.Alura.LiterAlura.service;

import br.com.Alura.LiterAlura.dto.AutorDTO;
import br.com.Alura.LiterAlura.dto.LivroDTO;
import br.com.Alura.LiterAlura.dto.ResultadoBusca;
import br.com.Alura.LiterAlura.model.Autor;
import br.com.Alura.LiterAlura.model.Livro;
import br.com.Alura.LiterAlura.repository.AutorRepository;
import br.com.Alura.LiterAlura.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {
    private final consumoAPI consumoAPI;
    private final ConverteDados conversor;
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    private final String ENDERECO_API = "https://gutendex.com/books/?search=";

    public LivroService(consumoAPI consumoAPI, ConverteDados conversor,
                        LivroRepository livroRepository, AutorRepository autorRepository) {
        this.consumoAPI = consumoAPI;
        this.conversor = conversor;
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }
   //OPCAO 1
    @Transactional
    public Optional<LivroDTO> buscarLivroPorTituloESalvar(String titulo) {
        Optional<Livro> livroExistente = livroRepository.findByTitleContainsIgnoreCase(titulo);
        if (livroExistente.isPresent()) {
            System.out.println("Livro já encontrado no banco de dados: " + livroExistente.get().getTitle());
            return Optional.of(new LivroDTO(
                    livroExistente.get().getTitle(),
                    livroExistente.get().getAuthors().stream()
                            .map(a -> new AutorDTO(a.getName(), a.getBirth_year(), a.getDeath_year()))
                            .collect(Collectors.toList()),
                    livroExistente.get().getLanguages(),
                    livroExistente.get().getDownload_count()
            ));
        }

        String json = consumoAPI.obterDados(ENDERECO_API + titulo.replace(" ", "%20"));
        ResultadoBusca resultado = conversor.obterDados(json, ResultadoBusca.class);

        if (resultado != null && !resultado.resultados().isEmpty()) {
            LivroDTO livroDTO = resultado.resultados().get(0);

            Livro livro = new Livro(
                    livroDTO.titulo(),
                    null,
                    livroDTO.idiomas(),
                    livroDTO.numeroDownloads()
            );

            try {
                livroRepository.save(livro);
            } catch (Exception e) {
                System.err.println("Erro ao salvar livro: Título já existe ou outro erro de DB. " + e.getMessage());
                return Optional.empty();
            }

            livroDTO.autores().forEach(aDto -> {
                Optional<Autor> autorExistente = autorRepository.findByNameContainsIgnoreCase(aDto.nome());
                Autor autor;
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();
                } else {
                    autor = new Autor(aDto.nome(), aDto.anoNascimento(), aDto.anoFalecimento());
                    autorRepository.save(autor);
                }
                livro.addAutor(autor);
            });

            try {
                livroRepository.save(livro);
                System.out.println("Livro salvo com sucesso no banco de dados: " + livro.getTitle());
            } catch (Exception e) {
                System.err.println("Erro ao salvar livro (relacionamentos): " + e.getMessage());
            }

            return Optional.of(livroDTO);
        } else {
            return Optional.empty();
        }
    }
    //OPCAO 2
    public List<Livro> listarTodosOsLivros() {
        return livroRepository.findAll();
    }
    //OPCAO 5
    public List<Livro> listarLivrosPorIdioma(String idioma) {
        return livroRepository.findByIdioma(idioma);
    }
}