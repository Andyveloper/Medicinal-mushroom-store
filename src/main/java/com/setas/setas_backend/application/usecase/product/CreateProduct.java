package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.product.ICreateProduct;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateProduct implements ICreateProduct {
    private final IProductRepository productRepository;

    @Override
    public Product execute(Product product) {
        if(product.getActive() == null) {
            product.setActive(true);
        }
        return productRepository.save(product);
    }

}
