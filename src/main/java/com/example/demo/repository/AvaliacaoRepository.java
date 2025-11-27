package com.example.demo.repository;

import com.example.demo.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByFilmeId(Long filmeId);

    Optional<Avaliacao> findByUsuarioComumIdAndFilmeId(Long usuarioId, Long filmeId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.filme.id = :filmeId")
    Double calcularMediaPorFilme(@Param("filmeId") Long filmeId);
}
