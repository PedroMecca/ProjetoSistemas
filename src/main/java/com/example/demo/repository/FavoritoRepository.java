package com.example.demo.repository;

import com.example.demo.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {}
