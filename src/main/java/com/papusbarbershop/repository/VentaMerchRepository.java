package com.papusbarbershop.repository;

import com.papusbarbershop.entity.VentaMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaMerchRepository extends JpaRepository<VentaMerch, Long> {

    @Query("SELECT v FROM VentaMerch v WHERE v.fecha BETWEEN :inicio AND :fin ORDER BY v.fecha DESC, v.hora DESC")
    List<VentaMerch> findByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT v FROM VentaMerch v WHERE v.fecha BETWEEN :inicio AND :fin AND (:categoria IS NULL OR v.categoria = :categoria) ORDER BY v.fecha DESC, v.hora DESC")
    List<VentaMerch> findByFechaBetweenAndCategoria(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("categoria") String categoria);
}
