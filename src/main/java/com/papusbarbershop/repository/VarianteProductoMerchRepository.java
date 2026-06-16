package com.papusbarbershop.repository;

import com.papusbarbershop.entity.VarianteProductoMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteProductoMerchRepository extends JpaRepository<VarianteProductoMerch, Long> {
    List<VarianteProductoMerch> findByProductoId(Long productoId);
    Optional<VarianteProductoMerch> findByIdAndProductoId(Long id, Long productoId);
}
