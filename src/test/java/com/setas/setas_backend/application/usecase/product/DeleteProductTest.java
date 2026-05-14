package com.setas.setas_backend.application.usecase.product;

import com.setas.setas_backend.domain.port.out.IProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProductTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private DeleteProduct deleteProduct;

    @Test
    void execute_deberiaDelegarElBorradoAlRepositorio() {
        deleteProduct.execute(1L);

        verify(productRepository).deleteById(1L);
    }
}
