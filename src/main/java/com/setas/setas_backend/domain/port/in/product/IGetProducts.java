package com.setas.setas_backend.domain.port.in.product;

import com.setas.setas_backend.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface IGetProducts {
    List<Product> getAll();
    Optional<Product> getById(Long id);
    List<Product> getAllActive();
}
