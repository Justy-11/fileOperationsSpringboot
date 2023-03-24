package com.jathursh.file_upload_download.controller;

import com.jathursh.file_upload_download.dto.FileUploadResponse;
import com.jathursh.file_upload_download.service.FileStorageService;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class FileStorageController {

    private FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/single/upload")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file){

        String fileName = fileStorageService.storeFile(file);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()  // https://localhost:9090
                .path("/download")  // append download to above current context path // https://localhost:9090/download
                .path(fileName)  // append file name to above  // https://localhost:9090/download/abc.jpg
                .toUriString();  // convert to url string  // https://localhost:9090/download/abc.jpg

        return new FileUploadResponse(fileName, file.getContentType(), url);
    }

    @GetMapping("/download/{fileName}")
    ResponseEntity<UrlResource> downloadSingleFile(@PathVariable String fileName){

        UrlResource resource = fileStorageService.downloadFile(fileName);

        MediaType contentType = MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:fileName=" + resource.getFilename())
                .body(resource);
    }
}
