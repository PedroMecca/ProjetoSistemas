package com.example.demo.repository;

import com.example.demo.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByFilmeId(Long filmeId);

    Optional<Avaliacao> findByUsuarioComumIdAndFilmeId(Long usuarioId, Long filmeId);
}
