package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.product.IUpdateProduct;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProduct implements IUpdateProduct {
  private final IProductRepository productRepository;

  @Override
  @Transactional
  public Product execute(Long id, Integer stock) {
    Product created = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    created.setStock(stock);
    return productRepository.update(created);
  }
}
