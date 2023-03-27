package com.jathursh.file_upload_download.controller;

import com.jathursh.file_upload_download.dto.FileDocument;
import com.jathursh.file_upload_download.dto.FileUploadResponse;
import com.jathursh.file_upload_download.service.DocFileDao;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

// 27/3
@RestController
public class UploadDownloadWithDatabaseController {

    private DocFileDao docFileDao;

    public UploadDownloadWithDatabaseController(DocFileDao docFileDao) {
        this.docFileDao = docFileDao;
    }

    @PostMapping("/single/uploadToDB")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        String name = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        FileDocument fileDocument = new FileDocument();
        fileDocument.setFileName(name);
        fileDocument.setDocFile(file.getBytes());

        docFileDao.save(fileDocument);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()  // https://localhost:9090
                .path("/downloadFromDB/")  // append download to above current context path // https://localhost:9090/downloadFromDB
                .path(name) // append file name to above  // https://localhost:9090/downloadFromDB/abc.jpg
                .toUriString();  // convert to url string  // https://localhost:9090/downloadFromDB/abc.jpg

        return new FileUploadResponse(name, file.getContentType(), url);
    }

    @GetMapping("/downloadFromDB/{fileName}")
    ResponseEntity<byte[]> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) throws MalformedURLException {

        FileDocument fileDocument = docFileDao.findByFileName(fileName);
        String mimetype = request.getServletContext().getMimeType(fileDocument.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimetype))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + fileDocument.getFileName())  // to download the image
                //.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + fileDocument.getFileName())  // to render the file
                .body(fileDocument.getDocFile());
    }
}
