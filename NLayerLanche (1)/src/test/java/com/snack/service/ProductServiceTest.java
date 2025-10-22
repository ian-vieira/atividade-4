package com.snack.service;

import com.snack.entities.Product;
import com.snack.services.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private ProductService productService;

    @TempDir
    Path tempDir;

    private final String BANCO_IMAGENS_PATH = "C:\\Users\\aluno\\BancoImagens\\";

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @AfterEach
    void limparArquivos() throws IOException {
        Files.deleteIfExists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg"));
    }

    @Test
    void deveSalvarProdutoComImagemValida() throws IOException {
        Path imagem = tempDir.resolve("imagem_teste.jpg");
        Files.createFile(imagem);

        Product p = new Product(1, "Chocolate", 5.0f, imagem.toString());

        boolean resultado = productService.save(p);

        assertTrue(resultado);
        assertTrue(Files.exists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg")));
    }

    @Test
    void naoDeveSalvarProdutoComImagemInexistente() {
        Product p = new Product(1, "Invalido", 5.0f, "C:\\nao_existe.jpg");
        boolean resultado = productService.save(p);
        assertFalse(resultado);
    }

    @Test
    void deveAtualizarProdutoExistente() throws IOException {
        Path imagem = tempDir.resolve("imagem_original.jpg");
        Files.createFile(imagem);

        Product p = new Product(1, "Produto", 10.0f, imagem.toString());
        productService.save(p);

        Path novaImagem = tempDir.resolve("imagem_nova.jpg");
        Files.createFile(novaImagem);

        p.setImage(novaImagem.toString());
        productService.update(p);

        assertTrue(Files.exists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg")));
    }

    @Test
    void deveRemoverProdutoExistente() throws IOException {
        Path imagem = tempDir.resolve("imagem_remove.jpg");
        Files.createFile(imagem);

        Product p = new Product(1, "Produto", 10.0f, imagem.toString());
        productService.save(p);

        productService.remove(1);

        assertFalse(Files.exists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg")));
    }

    @Test
    void deveObterCaminhoDaImagemPorId() throws IOException {
        Path imagem = tempDir.resolve("imagem_path.jpg");
        Files.createFile(imagem);

        Product p = new Product(1, "Produto", 10.0f, imagem.toString());
        productService.save(p);

        String caminho = productService.getImagePathById(1);
        assertNotNull(caminho);
    }
}
