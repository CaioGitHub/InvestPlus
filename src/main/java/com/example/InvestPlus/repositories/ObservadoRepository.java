package com.example.InvestPlus.repositories;

import com.example.InvestPlus.models.Observado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObservadoRepository extends JpaRepository<Observado, Long> {
    Optional<Observado> findBySymbol(String symbol);
    List<Observado> findAllByCategory(String category);
}
