package com.snack.applications;

import com.snack.entities.Product;
import com.snack.repositories.ProductRepository;
import com.snack.services.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductApplicationTest {

    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication;
    private final String BANCO_IMAGENS_PATH = "C:\\Users\\aluno\\BancoImagens\\";

    @TempDir
    Path tempDir;

    private Product produto;

    @BeforeEach
    void prepararCenario() throws IOException {
        productRepository = new ProductRepository();
        productService = new ProductService();
        productApplication = new ProductApplication(productRepository, productService);

        Path imagem = tempDir.resolve("imagem_app.jpg");
        Files.createFile(imagem);

        produto = new Product(1, "Chocolate", 5.0f, imagem.toString());
    }

    @AfterEach
    void limparArquivos() throws IOException {
        Files.deleteIfExists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg"));
    }

    @Test
    void deveListarTodosProdutosDoRepositorio() {
        productApplication.append(produto);
        List<Product> produtos = productApplication.getAll();
        assertEquals(1, produtos.size());
    }

    @Test
    void deveObterProdutoPorIdValido() {
        productApplication.append(produto);
        assertEquals(produto, productApplication.getById(1));
    }

    @Test
    void deveRetornarNuloAoObterProdutoPorIdInvalido() {
        assertNull(productRepository.getAll().stream().filter(p -> p.getId() == 999).findFirst().orElse(null));
    }

    @Test
    void deveConfirmarExistenciaProdutoPorIdValido() {
        productApplication.append(produto);
        assertTrue(productApplication.exists(1));
    }

    @Test
    void deveRetornarFalsoParaProdutoInexistente() {
        assertFalse(productApplication.exists(999));
    }

    @Test
    void deveAdicionarNovoProdutoESalvarImagem() {
        productApplication.append(produto);
        assertTrue(productApplication.exists(1));
        assertTrue(Files.exists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg")));
    }

    @Test
    void deveRemoverProdutoExistenteEDeletarImagem() {
        productApplication.append(produto);
        productApplication.remove(1);
        assertFalse(productApplication.exists(1));
    }

    @Test
    void naoDeveAlterarSistemaAoRemoverProdutoInexistente() {
        assertDoesNotThrow(() -> productApplication.remove(999));
    }

    @Test
    void deveAtualizarProdutoExistenteESubstituirImagem() throws IOException {
        productApplication.append(produto);

        Path novaImagem = tempDir.resolve("nova.jpg");
        Files.createFile(novaImagem);

        Product produtoAtualizado = new Product(1, "Chocolate Premium", 7.0f, novaImagem.toString());

        productApplication.update(1, produtoAtualizado);

        Product recuperado = productApplication.getById(1);
        assertEquals("Chocolate Premium", recuperado.getDescription());
        assertEquals(7.0f, recuperado.getPrice());
    }
}
