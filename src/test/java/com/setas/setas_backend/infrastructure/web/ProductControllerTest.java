package com.setas.setas_backend.infrastructure.web;

import com.setas.setas_backend.domain.model.Product;
import com.setas.setas_backend.domain.port.in.product.ICreateProduct;
import com.setas.setas_backend.domain.port.in.product.IDeleteProduct;
import com.setas.setas_backend.domain.port.in.product.IGetProducts;
import com.setas.setas_backend.domain.port.in.product.IUpdateProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock private ICreateProduct createProduct;
    @Mock private IDeleteProduct deleteProduct;
    @Mock private IGetProducts getProducts;
    @Mock private IUpdateProduct updateProduct;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private final Product reishi = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(10).active(true).build();
    private final Product chaga  = Product.builder().id(2L).name("Chaga").price(new BigDecimal("30.00")).stock(5).active(true).build();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void create_shouldReturn201WithCreatedProduct() throws Exception {
        when(createProduct.execute(any(Product.class))).thenReturn(reishi);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Reishi\",\"price\":25.00,\"stock\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Reishi"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(deleteProduct).execute(1L);
    }

    @Test
    void getAll_shouldReturn200WithProductList() throws Exception {
        when(getProducts.getAll()).thenReturn(List.of(reishi, chaga));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_shouldReturn200WhenProductExists() throws Exception {
        when(getProducts.getById(1L)).thenReturn(Optional.of(reishi));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Reishi"));
    }

    @Test
    void getById_shouldReturn404WhenProductNotFound() throws Exception {
        when(getProducts.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllActive_shouldReturn200WithActiveProducts() throws Exception {
        when(getProducts.getAllActive()).thenReturn(List.of(reishi));

        mockMvc.perform(get("/api/products/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateStock_shouldReturn200WithUpdatedProduct() throws Exception {
        Product updated = Product.builder().id(1L).name("Reishi").price(new BigDecimal("25.00")).stock(50).active(true).build();
        when(updateProduct.execute(eq(1L), eq(50))).thenReturn(updated);

        mockMvc.perform(patch("/api/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stock\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50));
    }
}
