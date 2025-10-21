package com.snack.applications;

import com.snack.entities.Product;
import com.snack.repositories.ProductRepository;
import com.snack.services.ProductService;
import com.snack.applications.ProductApplication;
import com.snack.facade.ProductFacade;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductFacadeTest {

    private ProductRepository productRepository;
    private ProductService productService;
    private ProductApplication productApplication;
    private ProductFacade productFacade;
    private Product product1;
    private Product product2;

    @TempDir
    Path tempDir;

    private final String BANCO_IMAGENS_PATH = "C:\\Users\\aluno\\BancoImagens\\";

    @BeforeEach
    public void setUp() {
        productRepository = new ProductRepository();
        productService = new ProductService();
        productApplication = new ProductApplication(productRepository, productService);
        productFacade = new ProductFacade(productApplication);

        product1 = new Product(1, "Cachorro quente", 4.00f, "C:\\caminho\\hotdog.jpg");
        product2 = new Product(2, "Hamburguer", 30.00f, "C:\\caminho\\hamburguer.jpg");
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(BANCO_IMAGENS_PATH + "1.jpg"));
        Files.deleteIfExists(Paths.get(BANCO_IMAGENS_PATH + "2.jpg"));
    }

    @Test
    public void deveRetornarListaCompletaDeProdutos() {
        productFacade.append(product1);
        productFacade.append(product2);
        List<Product> products = productFacade.getAll();

        assertEquals(2, products.size());
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    public void deveRetornarProdutoCorretoPorIdValido() {
        productFacade.append(product1);
        Product found = productFacade.getById(1);
        assertNotNull(found);
        assertEquals(product1, found);
        assertEquals("Cachorro quente", found.getDescription());
    }

    @Test
    public void deveRetornarTrueParaIdExistente() {
        productFacade.append(product1);
        assertTrue(productFacade.exists(1));
    }

    @Test
    public void deveRetornarFalseParaIdInexistente() {
        productFacade.append(product1);
        assertFalse(productFacade.exists(99));
    }

    @Test
    public void deveAdicionarProdutoCorretamenteESalvarImagem() throws IOException {
        Path imagemFonte = tempDir.resolve("facade_append_test.jpg");
        Files.createFile(imagemFonte);

        Product produto = new Product(1, "Produto Facade", 10.00f, imagemFonte.toString());
        Path imagemDestino = Paths.get(BANCO_IMAGENS_PATH + "1.jpg");

        Files.deleteIfExists(imagemDestino);

        productFacade.append(produto);

        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(imagemDestino));
    }

    @Test
    public void deveRemoverProdutoExistenteEDeletarImagem() throws IOException {
        Path imagemFonte = tempDir.resolve("facade_remove_test.jpg");
        Files.createFile(imagemFonte);

        Product produto = new Product(1, "Remover", 10.00f, imagemFonte.toString());
        Path imagemDestino = Paths.get(BANCO_IMAGENS_PATH + "1.jpg");

        productFacade.append(produto);

        assertTrue(productRepository.exists(1));
        assertTrue(Files.exists(imagemDestino));

        productFacade.remove(1);

        assertFalse(productRepository.exists(1));
        assertFalse(Files.exists(imagemDestino));
    }

    @Test
    public void deveCalcularOValorDaVendaCorretamente() {
        productFacade.append(product1);
        float valorTotal = productFacade.sellProduct(1, 5);
        assertEquals(20.0f, valorTotal);
    }

    @Test
    public void deveLancarExcecaoAoVenderProdutoInexistente() {
        assertThrows(NoSuchElementException.class, () -> productFacade.sellProduct(99, 2));
    }

    @Test
    public void deveAtualizarUmProdutoCorretamentePelaFacade() throws IOException {
        Path imagemOriginal = tempDir.resolve("original.jpg");
        Files.createFile(imagemOriginal);

        Product produtoOriginal = new Product(1, "Antigo", 10.00f, imagemOriginal.toString());
        productFacade.append(produtoOriginal);

        Path imagemNova = tempDir.resolve("nova.jpg");
        Files.createFile(imagemNova);

        Product produtoAtualizado = new Product(1, "Novo", 20.00f, imagemNova.toString());
        productFacade.update(1, produtoAtualizado);

        Product verificado = productRepository.getById(1);
        assertEquals("Novo", verificado.getDescription());
        assertEquals(20.00f, verificado.getPrice());
    }
}
