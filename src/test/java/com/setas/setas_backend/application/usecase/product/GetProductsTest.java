package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductsTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private GetProducts getProducts;

    private final Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();
    private final Product chaga  = Product.builder().id(2L).name("Chaga").price(new BigDecimal("30.00")).stock(0).active(false).build();

    @Test
    void getAll_deberiaRetornarTodosLosProductos() {
        when(productRepository.findAll()).thenReturn(List.of(reishi, chaga));

        List<Product> result = getProducts.getAll();

        assertThat(result).hasSize(2).containsExactly(reishi, chaga);
    }

    @Test
    void getById_deberiaRetornarProductoCuandoExiste() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(reishi));

        Optional<Product> result = getProducts.getById(1L);

        assertThat(result).isPresent().contains(reishi);
    }

    @Test
    void getById_deberiaRetornarVacioCuandoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = getProducts.getById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllActive_deberiaRetornarSoloProductosActivos() {
        when(productRepository.findAllActive()).thenReturn(List.of(reishi));

        List<Product> result = getProducts.getAllActive();

        assertThat(result).hasSize(1).first().extracting(Product::getActive).isEqualTo(true);
    }
}
