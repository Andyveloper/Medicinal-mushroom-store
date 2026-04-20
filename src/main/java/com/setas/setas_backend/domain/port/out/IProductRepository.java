package com.setas.setas_backend.domain.port.out;

import com.setas.setas_backend.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductRepository {

    Product save(Product product);
    void deleteById(Long id);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findAllActive();

}
