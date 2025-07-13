package br.com.Alura.LiterAlura.Principal;

import br.com.Alura.LiterAlura.dto.LivroDTO;
import br.com.Alura.LiterAlura.model.Livro;
import br.com.Alura.LiterAlura.model.Autor;
import br.com.Alura.LiterAlura.service.LivroService;
import br.com.Alura.LiterAlura.service.AutorService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private LivroService livroService;
    private AutorService autorService;

    public Principal(LivroService livroService, AutorService autorService) {
        this.livroService = livroService;
        this.autorService = autorService;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 6) {
            var menu = """
                    **************************************************
                    Escolha o número da sua opção:
                    1- Buscar livro pelo Título
                    2- Listar livros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos em determinado ano
                    5- Listar livros em um determinado idioma
                    6- Sair
                    **************************************************
                    """;
            System.out.println(menu);
            try {
                opcao = Integer.parseInt(teclado.nextLine());

                switch (opcao) {
                    case 1:
                        buscarLivroPorTituloESalvar();
                        break;
                    case 2:
                        listarLivrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosEmAno();
                        break;
                    case 5:
                        listarLivrosPorIdioma();
                        break;
                    case 6:
                        System.out.println("Saindo da aplicação. Até mais!");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
            }
        }
    }

    private void buscarLivroPorTituloESalvar() {
        System.out.println("Digite o título do livro que deseja buscar:");
        var tituloLivro = teclado.nextLine();

        Optional<LivroDTO> livroEncontrado = livroService.buscarLivroPorTituloESalvar(tituloLivro);

        if (livroEncontrado.isPresent()) {
            LivroDTO livro = livroEncontrado.get();
            System.out.println("\n----- Livro Encontrado e (Se Novo) Salvo -----");
            System.out.println("Título: " + livro.titulo());
            if (livro.autores() != null && !livro.autores().isEmpty()) {
                livro.autores().forEach(a -> System.out.println("  Autor: " + a.nome() +
                        " (Nasc: " + (a.anoNascimento() != null ? a.anoNascimento() : "N/A") +
                        " / Falec: " + (a.anoFalecimento() != null ? a.anoFalecimento() : "N/A") + ")"));
            }
            if (livro.idiomas() != null && !livro.idiomas().isEmpty()) {
                System.out.println("  Idiomas: " + String.join(", ", livro.idiomas()));
            }
            System.out.println("  Número de Downloads: " + (livro.numeroDownloads() != null ? livro.numeroDownloads() : "N/A")); // Exibir novo atributo
            System.out.println("----------------------------------------------\n");
        } else {
            System.out.println("Livro não encontrado para o título: '" + tituloLivro + "'\n");
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = livroService.listarTodosOsLivros();

        if (livros.isEmpty()) {
            System.out.println("\nNenhum livro registrado no banco de dados ainda.\n");
        } else {
            System.out.println("\n----- Livros Registrados -----");
            livros.forEach(livro -> {
                System.out.println("Título: " + livro.getTitle());
                if (livro.getAuthors() != null && !livro.getAuthors().isEmpty()) {
                    livro.getAuthors().forEach(autor -> System.out.println("  Autor: " + autor.getName() +
                            " (Nasc: " + (autor.getBirth_year() != null ? autor.getBirth_year() : "N/A") +
                            " / Falec: " + (autor.getDeath_year() != null ? autor.getDeath_year() : "N/A") + ")"));
                }
                if (livro.getLanguages() != null && !livro.getLanguages().isEmpty()) {
                    System.out.println("  Idiomas: " + String.join(", ", livro.getLanguages()));
                }
                System.out.println("  Número de Downloads: " + (livro.getDownload_count() != null ? livro.getDownload_count() : "N/A")); // Exibir novo atributo
                System.out.println("------------------------------");
            });
            System.out.println("------------------------------\n");
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorService.listarTodosOsAutores();

        if (autores.isEmpty()) {
            System.out.println("\nNenhum autor registrado no banco de dados ainda.\n");
        } else {
            System.out.println("\n----- Autores Registrados e Seus Livros -----");
            autores.forEach(autor -> {
                System.out.println("Nome: " + autor.getName());
                System.out.println("Ano de Nascimento: " + (autor.getBirth_year() != null ? autor.getBirth_year() : "N/A"));
                System.out.println("Ano de Falecimento: " + (autor.getDeath_year() != null ? autor.getDeath_year() : "N/A"));

                if (autor.getLivros() != null && !autor.getLivros().isEmpty()) {
                    String titulosLivros = autor.getLivros().stream()
                            .map(Livro::getTitle)
                            .collect(Collectors.joining("; "));
                    System.out.println("  Livros: [" + titulosLivros + "]");
                } else {
                    System.out.println("  Livros: [Nenhum livro cadastrado para este autor]");
                }
                System.out.println("------------------------------");
            });
            System.out.println("------------------------------\n");
        }
    }

    private void listarAutoresVivosEmAno() {
        System.out.println("Digite o ano para verificar autores vivos:");
        try {
            var ano = Integer.parseInt(teclado.nextLine());
            List<Autor> autoresVivos = autorService.listarAutoresVivosEmAno(ano);

            if (autoresVivos.isEmpty()) {
                System.out.println("\nNenhum autor vivo encontrado no ano " + ano + ".\n");
            } else {
                System.out.println("\n----- Autores Vivos em " + ano + " -----");
                autoresVivos.forEach(autor -> {
                    System.out.println("Nome: " + autor.getName());
                    System.out.println("Ano de Nascimento: " + (autor.getBirth_year() != null ? autor.getBirth_year() : "N/A"));
                    System.out.println("Ano de Falecimento: " + (autor.getDeath_year() != null ? autor.getDeath_year() : "N/A"));
                    System.out.println("------------------------------");
                });
                System.out.println("------------------------------\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido. Por favor, digite um número.\n");
        }
    }
    private void listarLivrosPorIdioma() {
        System.out.println("\n----- Idiomas Disponíveis -----");
        System.out.println("es - Espanhol");
        System.out.println("pt - Português");
        System.out.println("en - Inglês");
        System.out.println("fr - Francês");
        System.out.println("------------------------------");
        System.out.println("Digite a sigla do idioma desejado (ex: es, pt, en, fr):");

        String idiomaSelecionado = teclado.nextLine().toLowerCase();

        if (!List.of("es", "pt", "en", "fr").contains(idiomaSelecionado)) {
            System.out.println("Sigla de idioma inválida. Por favor, digite uma das opções: es, pt, en, fr.\n");
            return;
        }

        List<Livro> livrosPorIdioma = livroService.listarLivrosPorIdioma(idiomaSelecionado);

        if (livrosPorIdioma.isEmpty()) {
            System.out.println("\nNenhum livro encontrado para o idioma '" + idiomaSelecionado + "'.\n");
        } else {
            System.out.println("\n----- Livros no Idioma '" + idiomaSelecionado + "' -----");
            livrosPorIdioma.forEach(livro -> {
                System.out.println("Título: " + livro.getTitle());
                if (livro.getAuthors() != null && !livro.getAuthors().isEmpty()) {
                    livro.getAuthors().forEach(autor -> System.out.println("  Autor: " + autor.getName() +
                            " (Nasc: " + (autor.getBirth_year() != null ? autor.getBirth_year() : "N/A") +
                            " / Falec: " + (autor.getDeath_year() != null ? autor.getDeath_year() : "N/A") + ")"));
                }
                System.out.println("  Idiomas: " + String.join(", ", livro.getLanguages()));
                System.out.println("  Número de Downloads: " + (livro.getDownload_count() != null ? livro.getDownload_count() : "N/A"));
                System.out.println("------------------------------");
            });
            System.out.println("------------------------------\n");
        }

}}