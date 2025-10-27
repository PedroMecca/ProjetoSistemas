package com.example.demo.repository;

import com.example.demo.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmeRepository extends JpaRepository<Filme, Integer> {}
