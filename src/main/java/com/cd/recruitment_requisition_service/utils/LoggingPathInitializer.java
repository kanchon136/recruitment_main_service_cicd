package com.cd.recruitment_requisition_service.utils;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LoggingPathInitializer {

    @Value("${logging.file.name:}")
    String filePath;

    @PostConstruct
    public void setupLoggingPath() {
        try {
            // Get the drive where the app is running (e.g. "C:" or "D:")
            String currentDir = new File(".").getAbsolutePath();
            String rootDrive = currentDir.substring(0, 2);

            // Define your custom log folder path
            String logFolderPath = rootDrive + filePath;

            File logFolder = new File(logFolderPath);
            if (!logFolder.exists()) {
                boolean created = logFolder.mkdirs();
                if (created) {
                    System.out.println("✅ Log folder created at: " + logFolder.getAbsolutePath());
                } else {
                    System.err.println("⚠️ Failed to create log folder at: " + logFolder.getAbsolutePath());
                }
            }

            // Dynamically set system properties for Spring Boot logging
            System.setProperty("logging.file.name", logFolderPath);
            System.setProperty("logging.file.path", logFolderPath);

            System.out.println("📁 Logging initialized at: " + System.getProperty("logging.file.name"));

        } catch (Exception e) {
            System.err.println(" Error initializing log folder: " + e.getMessage());
        }
    }
}
