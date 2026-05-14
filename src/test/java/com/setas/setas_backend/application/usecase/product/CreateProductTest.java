package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.out.IProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private CreateProduct createProduct;

    @Test
    void execute_deberiaEstablecerActivoTrueCuandoEsNull() {
        Product input = new Product();
        input.setName("Reishi");
        input.setPrice(new BigDecimal("25.00"));
        input.setStock(10);

        Product saved = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = createProduct.execute(input);

        assertThat(input.getActive()).isTrue();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(input);
    }

    @Test
    void execute_deberiaConservarActivoCuandoYaEstaEstablecido() {
        Product input = Product.builder().name("Chaga").price(new BigDecimal("30.00")).stock(5).active(false).build();
        when(productRepository.save(any(Product.class))).thenReturn(input);

        Product result = createProduct.execute(input);

        assertThat(result.getActive()).isFalse();
    }
}
