package com.cd.recruitment_requisition_service.serviceImpl;


import com.cd.recruitment_requisition_service.enums.ServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class StorageService {

     public static final String baseDirectory = File.separator + "mnt" + File.separator + "recruitment_management_system_file";
   // public static final String baseDirectory = File.separator + "opt" + File.separator + "recruitment_backend";


    public static String copyFile(MultipartFile multifile, String subFolder) {
        if (multifile == null || multifile.isEmpty()) {
            return null;
        }
        try {
            String modifyBaseDerectory = baseDirectory + File.separator + subFolder;
            log.info("modifyBaseDerectory when call copyFile==> " + modifyBaseDerectory);

            Path fileStorageLocation = Paths.get(modifyBaseDerectory).toAbsolutePath().normalize();

            // ১. ডিরেক্টরি তৈরি করুন
            Files.createDirectories(fileStorageLocation);

            String filename = Objects.requireNonNull(multifile.getOriginalFilename());
            String cleanedFilename = filename.trim().replaceAll("\\s+", "_");

            Path targetLocation = fileStorageLocation.resolve(cleanedFilename);

            // ২. ফাইলটি কপি করুন
            Files.copy(multifile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // ৩. লিনাক্স রিড পারমিশন সেট করা (৭৫৫ এর কাজ করবে)
            File savedFile = targetLocation.toFile();
            savedFile.setReadable(true, false); // সবার জন্য রিড পারমিশন (Readable for everyone)
            savedFile.setExecutable(true, false); // সবার জন্য এক্সেকিউটেবল (প্রয়োজনীয় ফোল্ডার ট্রাভার্সাল এর জন্য)

            log.info("File saved and permissions set at: {}", targetLocation.toString());

            return cleanedFilename;

        } catch (IOException ex) {
            log.error("I/O error while saving file '{}': {}", multifile.getOriginalFilename(), ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error while saving file '{}': {}", multifile.getOriginalFilename(), ex.getMessage(), ex);
        }

        return null;
    }
//
//    public static Resource loadFileAsResource(String dynamicId, String fileName, ServiceType serviceType) {
//        try {
//
//            String modifyBaseDirectory = "";
//            if (serviceType == ServiceType.RECRUITMENT_REQUISITION_SERVICE) {
//                modifyBaseDirectory = baseDirectory + File.separator + "RECRUITMENT_REQUISITION" + File.separator + dynamicId;
//            } else if (serviceType == ServiceType.RECRUITMENT_MANPOWER_PLANNING_SERVICE) {
//                modifyBaseDirectory = baseDirectory + File.separator + "RECRUITMENT_MANPOWER_PLANNING" + File.separator + dynamicId;
//            } else if (serviceType == ServiceType.RECRUITMENT_MASTER_SERVICE) {
//                modifyBaseDirectory = baseDirectory + File.separator + "RECRUITMENT_MASTER" + File.separator + dynamicId;
//
//            }
//            log.info("modifyBaseDirectory when call loadFileAsResource==>" + modifyBaseDirectory);
//
//            Path fileStorageLocation = Paths.get(modifyBaseDirectory).toAbsolutePath().normalize();
//            Path filePath = fileStorageLocation.resolve(fileName).normalize();
//            log.info("Absolute File Path: " + filePath.toAbsolutePath());
//            Resource resource = new UrlResource(filePath.toUri());
//            if (resource.exists() && resource.isReadable()) {
//                return resource;
//            } else {
//                return null;
//            }
//        } catch (MalformedURLException ex) {
//            log.error("Malformed URL for file '{}': {}", fileName, ex.getMessage());
//            return null;
//        }
//    }

    public static Resource loadFileAsResource(String dynamicId, String fileName, ServiceType serviceType) {
        try {
            String subFolder = "";
            if (serviceType == ServiceType.RECRUITMENT_REQUISITION_SERVICE) {
                subFolder = "RECRUITMENT_REQUISITION";
            } else if (serviceType == ServiceType.RECRUITMENT_MANPOWER_PLANNING_SERVICE) {
                subFolder = "RECRUITMENT_MANPOWER_PLANNING";
            } else if (serviceType == ServiceType.RECRUITMENT_MASTER_SERVICE) {
                subFolder = "RECRUITMENT_MASTER";
            }

            // লিনাক্সে পাথ হ্যান্ডেল করার জন্য সরাসরি Paths.get ব্যবহার করা সবচেয়ে নিরাপদ।
            // এটি baseDirectory (/mnt/...) থেকে শুরু করে পূর্ণাঙ্গ পাথ তৈরি করবে।
            Path filePath = Paths.get(baseDirectory, subFolder, dynamicId, fileName).normalize();

            log.info("filePathForTest: " + filePath.toString());

            // UrlResource তৈরি করার আগে ফাইলটি আসলেও ঐ লোকেশনে আছে কি না তা একবার চেক করে নেওয়া
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                log.info("File successfully found and is readable: " + filePath.toString());
                return resource;
            } else {
                log.error("File NOT found or NOT readable at: " + filePath.toString());
                // লিনাক্সে ফাইল থাকলে কিন্তু রিড না হলে পারমিশন চেক করার জন্য এই লগটি কাজে লাগবে
                return null;
            }
        } catch (MalformedURLException ex) {
            log.error("Malformed URL for file '{}': {}", fileName, ex.getMessage());
            return null;
        } catch (Exception ex) {
            log.error("An unexpected error occurred while loading file: {}", ex.getMessage());
            return null;
        }
    }

}
