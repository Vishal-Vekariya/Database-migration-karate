//package com.karate.kht.controller;
//
//import com.karate.kht.entity.FileEntity;
//import com.karate.kht.entity.UserEntity;
//import com.karate.kht.service.FileService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping(value = "/files", produces = MediaType.APPLICATION_JSON_VALUE)
//@RequiredArgsConstructor
//public class FileController {
//    private final FileService fileService;
//
//    @GetMapping
//    public List<FileEntity> getFiles() {
//        return fileService.getFiles();
//    }
//
//    @GetMapping("/{id}")
//    public FileEntity getFile(@PathVariable Long id) {
//        return fileService.getFileById(id);
//    }
//
//    @GetMapping("/count")
//    public Long getFileCount() {
//        return fileService.getFileCount();
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<FileEntity> uploadFile(@RequestParam("file") MultipartFile file,
//                                                 @AuthenticationPrincipal UserEntity user) throws IOException {
//        FileEntity uploadedFile = fileService.uploadFile(file, user);
//        return ResponseEntity.ok(uploadedFile);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
//if (fileService.getFileById(id) != null) {
//        fileService.deleteFile(id);
//            return ResponseEntity.noContent().build();
//        }
//                return ResponseEntity.notFound().build();
//    }
//}
