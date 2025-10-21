package com.snack.repositories;

import com.snack.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {
    private ProductRepository productRepository;
    private Product product1;
    private Product product2;

    @BeforeEach
    void prepararCenario() {
        productRepository = new ProductRepository();
        product1 = new Product(1, "Chocolate", 5.0f, "c:\\img1.jpg");
        product2 = new Product(2, "Refrigerante", 8.0f, "c:\\img2.jpg");
    }

    @Test
    void deveConfirmarQueRepositorioIniciaVazio() {
        List<Product> produtos = productRepository.getAll();
        assertTrue(produtos.isEmpty(), "O repositório deve iniciar vazio");
    }

    @Test
    void deveAdicionarProdutoCorretamente() {
        productRepository.append(product1);
        assertEquals(1, productRepository.getAll().size());
        assertTrue(productRepository.exists(1));
    }

    @Test
    void deveRecuperarProdutoPorId() {
        productRepository.append(product1);
        Product encontrado = productRepository.getById(1);
        assertEquals(product1.getDescription(), encontrado.getDescription());
    }

    @Test
    void deveConfirmarExistenciaProduto() {
        productRepository.append(product1);
        assertTrue(productRepository.exists(1));
    }

    @Test
    void deveRemoverProdutoCorretamente() {
        productRepository.append(product1);
        productRepository.remove(1);
        assertFalse(productRepository.exists(1));
    }

    @Test
    void deveAtualizarProdutoCorretamente() {
        productRepository.append(product1);
        Product atualizado = new Product(1, "Chocolate Premium", 7.0f, "c:\\nova.jpg");
        productRepository.update(1, atualizado);

        Product recuperado = productRepository.getById(1);
        assertEquals("Chocolate Premium", recuperado.getDescription());
        assertEquals(7.0f, recuperado.getPrice());
    }

    @Test
    void deveRetornarTodosProdutosCorretamente() {
        productRepository.append(product1);
        productRepository.append(product2);
        assertEquals(2, productRepository.getAll().size());
    }

    @Test
    void naoDeveRemoverProdutoInexistente() {
        assertDoesNotThrow(() -> productRepository.remove(99));
    }

    @Test
    void naoDeveAtualizarProdutoInexistente() {
        assertThrows(Exception.class, () -> {
            Product p = new Product(99, "X", 1.0f, "");
            productRepository.update(99, p);
        });
    }

    @Test
    void naoDevePermitirIdsDuplicados() {
        productRepository.append(product1);
        productRepository.append(product1);
        assertEquals(1, productRepository.getAll().size());
    }
}
