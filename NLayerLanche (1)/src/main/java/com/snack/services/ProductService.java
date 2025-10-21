package com.snack.services;

import com.snack.entities.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public class ProductService {
    // Usa diretório temporário do sistema para salvar imagens
    private String filePath;

    public ProductService() {
        this.filePath = System.getProperty("java.io.tmpdir") + File.separator + "BancoImagens" + File.separator;
        try {
            Files.createDirectories(Paths.get(filePath)); // Cria a pasta caso não exista
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretório temporário para imagens", e);
        }
    }

    private String getFileExtension(Path path) {
        String filename = path.getFileName().toString();
        int lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1);
    }

    public boolean save(Product product) {
        Path path = Paths.get(product.getImage());
        Path destinationPath = Paths.get(String.format("%s%d.%s", filePath, product.getId(), getFileExtension(path)));

        if (Files.exists(path)) {
            try {
                Files.copy(path, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(destinationPath.toString());
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public String getImagePathById(int id) {
        File directory = new File(filePath);
        File[] matches = directory.listFiles((dir, name) -> name.startsWith(String.valueOf(id)));
        if (matches == null || matches.length == 0) {
            return null;
        }
        return Arrays.stream(matches).findFirst().get().getAbsolutePath();
    }

    public void update(Product product) {
        remove(product.getId());
        save(product);
    }

    public void remove(int id) {
        String imagePath = getImagePathById(id);
        if (imagePath == null) return;

        Path path = Paths.get(imagePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
