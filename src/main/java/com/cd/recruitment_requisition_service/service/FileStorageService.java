package com.cd.recruitment_requisition_service.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
     String storeFile(MultipartFile file, Long masterId);

    Resource loadFileAsResource(String filePath);

    void deleteFile(String filePath);
}
