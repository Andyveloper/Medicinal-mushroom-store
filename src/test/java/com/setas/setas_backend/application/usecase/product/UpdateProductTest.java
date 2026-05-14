package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private UpdateProduct updateProduct;

    @Test
    void execute_deberiaActualizarStockCorrectamente() {
        Product existing = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();
        Product updated  = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(50).active(true).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.update(any(Product.class))).thenReturn(updated);

        Product result = updateProduct.execute(1L, 50);

        assertThat(result.getStock()).isEqualTo(50);
        verify(productRepository).update(existing);
    }

    @Test
    void execute_deberiaLanzarExcepcionCuandoProductoNoExiste() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateProduct.execute(99L, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}
