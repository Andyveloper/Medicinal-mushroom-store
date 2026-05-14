package com.setas.setas_backend.infrastructure.persistence.product;

import com.setas.setas_backend.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock private JpaProductRepository jpaProductRepository;

    @InjectMocks
    private ProductRepositoryImpl productRepositoryImpl;

    private final Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();

    @Test
    void save_shouldDelegateToJpaRepository() {
        when(jpaProductRepository.save(reishi)).thenReturn(reishi);

        Product result = productRepositoryImpl.save(reishi);

        assertThat(result).isEqualTo(reishi);
        verify(jpaProductRepository).save(reishi);
    }

    @Test
    void deleteById_shouldDelegateToJpaRepository() {
        productRepositoryImpl.deleteById(1L);

        verify(jpaProductRepository).deleteById(1L);
    }

    @Test
    void findById_shouldReturnProductWhenExists() {
        when(jpaProductRepository.findById(1L)).thenReturn(Optional.of(reishi));

        Optional<Product> result = productRepositoryImpl.findById(1L);

        assertThat(result).isPresent().contains(reishi);
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        when(jpaProductRepository.findAll()).thenReturn(List.of(reishi));

        List<Product> result = productRepositoryImpl.findAll();

        assertThat(result).containsExactly(reishi);
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveProducts() {
        when(jpaProductRepository.findAllActive()).thenReturn(List.of(reishi));

        List<Product> result = productRepositoryImpl.findAllActive();

        assertThat(result).containsExactly(reishi);
    }

    @Test
    void update_shouldDelegateToJpaRepositorySave() {
        Product updated = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(50).active(true).build();
        when(jpaProductRepository.save(updated)).thenReturn(updated);

        Product result = productRepositoryImpl.update(updated);

        assertThat(result.getStock()).isEqualTo(50);
        verify(jpaProductRepository).save(updated);
    }
}
