package com.papusbarbershop.repository;

import com.papusbarbershop.entity.ImagenProductoMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenProductoMerchRepository extends JpaRepository<ImagenProductoMerch, Long> {
    List<ImagenProductoMerch> findByProductoIdOrderByOrdenAsc(Long productoId);
}
