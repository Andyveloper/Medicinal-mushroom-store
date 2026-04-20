package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.port.in.product.IDeleteProduct;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProduct implements IDeleteProduct {
    private final IProductRepository productRepository;

    @Override
    public void execute(Long id) {
        productRepository.deleteById(id);
    }
}
