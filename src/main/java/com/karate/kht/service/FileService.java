//package com.karate.kht.service;
//
//import com.azure.storage.blob.BlobClient;
//import com.azure.storage.blob.BlobContainerClient;
//import com.azure.storage.blob.BlobServiceClient;
//import com.azure.storage.blob.BlobServiceClientBuilder;
//import com.karate.kht.entity.FileEntity;
//import com.karate.kht.entity.UserEntity;
//import com.karate.kht.repository.FileRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class FileService {
//    private final FileRepository fileRepository;
//    private final BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().buildClient();
//    private final BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("account-name");
//
//    public List<FileEntity> getFiles() {
//        return fileRepository.findAll();
//    }
//
//    public FileEntity getFileById(Long id) {
//        return fileRepository
//                .findById(id)
//                .orElse(null);
//    }
//
//    public FileEntity uploadFile(MultipartFile file, UserEntity user) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : null;
//        String uniqueFileName = UUID.randomUUID() + fileExtension;
//
//        // Upload file to Azure Blob Storage
//        BlobClient blobClient = blobContainerClient.getBlobClient(uniqueFileName);
//        blobClient.upload(file.getInputStream(), file.getSize(), true);
//
//        // Save file metadata to database
//        FileEntity fileEntity = new FileEntity();
//        fileEntity.setUser(user);
//        fileEntity.setFileName(originalFilename);
//        fileEntity.setFileUrl(blobClient.getBlobUrl());
//        fileEntity.setUploadDate(LocalDateTime.now());
//
//        return fileRepository.save(fileEntity);
//    }
//
//    public void deleteFile(Long fileId) {
//        FileEntity fileEntity = fileRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        // Delete from Azure Blob Storage
//        blobContainerClient.getBlobClient(fileEntity.getFileUrl()).delete();
//
//        // Delete from Database
//        fileRepository.delete(fileEntity);
//    }
//
//    public Long getFileCount() {
//        return fileRepository.count();
//    }
//
//    // Utility method to check if a string is not null and not empty
//    private boolean isValid(String value) {
//        return value != null && !value.trim().isEmpty();
//    }
//}
