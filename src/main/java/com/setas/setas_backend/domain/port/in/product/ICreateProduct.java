package com.setas.setas_backend.domain.port.in.product;

import com.setas.setas_backend.domain.model.Product;

public interface ICreateProduct {
    Product execute(Product product);
}
