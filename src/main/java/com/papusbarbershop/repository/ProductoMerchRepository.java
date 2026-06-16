package com.papusbarbershop.repository;

import com.papusbarbershop.entity.ProductoMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoMerchRepository extends JpaRepository<ProductoMerch, Long> {
    List<ProductoMerch> findByActivoTrueOrderByNombreAsc();
    List<ProductoMerch> findAllByOrderByNombreAsc();
}
