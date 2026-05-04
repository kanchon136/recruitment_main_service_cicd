package com.cd.recruitment_requisition_service.serviceImpl;

import com.cd.recruitment_requisition_service.cumtomException.CustomException;
import com.cd.recruitment_requisition_service.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.rootStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootStorageLocation);
        } catch (IOException ex) {
            throw new CustomException("Could not create the root directory for file storage."+ex);
        }
    }

    @Override
     public String storeFile(MultipartFile file, Long masterId) {
        if (file.isEmpty()) {
            throw new CustomException("Cannot store empty file.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;

         String datePath = LocalDate.now().toString().replace("-", "/");
        String dynamicPath = masterId.toString() + "/" + datePath;

        Path targetDirectory = this.rootStorageLocation.resolve(dynamicPath);

        try {
             Files.createDirectories(targetDirectory);
            Path targetLocation = targetDirectory.resolve(uniqueFileName);

             Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

             return dynamicPath + "/" + uniqueFileName;

        } catch (IOException ex) {
            log.error("Error storing file: {}", originalFileName, ex);
            throw new CustomException("Could not store file " + originalFileName+ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = this.rootStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException("File not found: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new CustomException("File not found: " + filePath + ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null) return;
        try {
            Path fileToDelete = this.rootStorageLocation.resolve(filePath).normalize();
            Files.deleteIfExists(fileToDelete);
            log.info("Physical file deleted: {}", filePath);
        } catch (IOException e) {
            log.warn("Failed to delete physical file: {}", filePath, e);
            // Throw exception if deletion is critical, or just log warn
        }
    }
}
