package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório de upload", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normaliza o nome do arquivo
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Verifica se o arquivo contém caracteres inválidos
            if (originalFilename.contains("..")) {
                throw new RuntimeException("Nome de arquivo inválido: " + originalFilename);
            }

            // Gera um nome único para o arquivo
            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Copia o arquivo para o local de armazenamento
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao armazenar arquivo: " + originalFilename, ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Arquivo não encontrado: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Arquivo não encontrado: " + filename);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao deletar arquivo: " + filename, ex);
        }
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
