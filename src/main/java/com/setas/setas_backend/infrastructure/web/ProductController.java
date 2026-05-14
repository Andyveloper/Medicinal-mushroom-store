package com.setas.setas_backend.infrastructure.web;


import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.product.ICreateProduct;
import com.setas.setas_backend.domain.port.in.product.IDeleteProduct;
import com.setas.setas_backend.domain.port.in.product.IGetProducts;
import com.setas.setas_backend.domain.port.in.product.IUpdateProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ICreateProduct createProduct;
  private final IDeleteProduct deleteProduct;
  private final IGetProducts getProducts;
  private final IUpdateProduct updateProduct;

  @PostMapping
  public ResponseEntity<Product> create(@RequestBody Product product) {
    Product created = createProduct.execute(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    deleteProduct.execute(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<List<Product>> getAll() {
    return ResponseEntity.ok(getProducts.getAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getById(@PathVariable Long id) {
    return getProducts.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/active")
  public ResponseEntity<List<Product>> getAllActive() {
    return ResponseEntity.ok(getProducts.getAllActive());
  }

  @PatchMapping("/{id}/stock")
  public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
    return ResponseEntity.ok(updateProduct.execute(id, body.get("stock")));
  }
}
