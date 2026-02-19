package com.desafio.linx.api.repository;

import com.desafio.linx.api.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByDataEventoBetween(LocalDate inicio, LocalDate fim);
}