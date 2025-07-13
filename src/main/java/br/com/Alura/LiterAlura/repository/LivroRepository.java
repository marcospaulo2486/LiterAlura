package br.com.Alura.LiterAlura.repository;

import br.com.Alura.LiterAlura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByTitleContainsIgnoreCase(String title);

    @Query("SELECT l FROM Livro l JOIN FETCH l.authors WHERE :idioma MEMBER OF l.languages")
    List<Livro> findByIdioma(String idioma);
}
