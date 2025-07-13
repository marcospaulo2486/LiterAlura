package br.com.Alura.LiterAlura.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> authors = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "livro_idiomas", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "idioma")
    private List<String> languages;

    private Integer download_count;

    public Livro() {}

    public Livro(String title, List<Autor> authors, List<String> languages, Integer download_count) {
        this.title = title;
        this.languages = languages;
        this.download_count = download_count;
        if (authors != null) {
            authors.forEach(this::addAutor);
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Set<Autor> getAuthors() { return authors; }
    public void setAuthors(Set<Autor> authors) { this.authors = authors; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public Integer getDownload_count() { return download_count; }
    public void setDownload_count(Integer download_count) { this.download_count = download_count; }

    public void addAutor(Autor autor) {
        this.authors.add(autor);
        autor.getLivros().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return title != null ? title.equals(livro.title) : livro.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }

    @Override
    public String toString() {
        String autoresNomes = authors.stream()
                .map(Autor::getName)
                .collect(Collectors.joining(", "));
        String idiomasStr = languages != null ? String.join(", ", languages) : "N/A";

        return "Livro{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=[" + autoresNomes +
                "], languages=[" + idiomasStr +
                "], download_count=" + download_count +
                '}';
    }
}