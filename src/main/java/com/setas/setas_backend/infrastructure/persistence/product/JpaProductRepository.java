package com.setas.setas_backend.infrastructure.persistence.product;

import com.setas.setas_backend.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.active = true")
    List<Product> findAllActive();

}
