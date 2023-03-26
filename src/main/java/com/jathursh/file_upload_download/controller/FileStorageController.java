package com.jathursh.file_upload_download.controller;

import com.jathursh.file_upload_download.dto.FileUploadResponse;
import com.jathursh.file_upload_download.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class FileStorageController {

    private FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/single/upload")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file){

        String fileName = fileStorageService.storeFile(file);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()  // https://localhost:9090
                .path("/download/")  // append download to above current context path // https://localhost:9090/download
                .path(fileName)  // append file name to above  // https://localhost:9090/download/abc.jpg
                .toUriString();  // convert to url string  // https://localhost:9090/download/abc.jpg

        return new FileUploadResponse(fileName, file.getContentType(), url);
    }

    @GetMapping("/download/{fileName}")
    ResponseEntity<Resource> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) throws MalformedURLException {

        Resource resource = fileStorageService.downloadFile(fileName);

        /* MediaType contentType = MediaType.IMAGE_JPEG;  MediaType contentType = MediaType.APPLICATION_PDF;*/
        /* this is HARDCODING, so we can use http servlet request to get the content type according to the type of file we are uploading ---> */

        String mimetype;

        try {
            mimetype = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mimetype = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimetype))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename())  // to download the image
                //.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename())  // to render the file
                .body(resource);
    }

    @PostMapping("/multiple/upload")
    List<FileUploadResponse> multipleUpload(@RequestParam("files") MultipartFile[] files){

        // no. of files threshold
        if(files.length > 7){
            throw new RuntimeException("Too many files uploaded!!");
        }

        List<FileUploadResponse> uploadResponseList = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> {
                    String fileName = fileStorageService.storeFile(file);

                    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/download/")
                            .path(fileName)
                            .toUriString();

                    FileUploadResponse response=  new FileUploadResponse(fileName, file.getContentType(), url);
                    uploadResponseList.add(response);
                });

        return uploadResponseList;
    }

    // /zipDownload?fileName=create a mock server.png&fileName=DEPARTMENT_after_delete.png
    /*files - an array of file names to be included in the zip file.
    response - an instance of HttpServletResponse that is used to write the zip file to the HTTP response.
    Inside the method, a ZipOutputStream is created using the HttpServletResponse's output stream as its destination.
    For each file in the files array, the method downloads the file using the fileStorageService.downloadFile() method
    and adds it to the zip file as a ZipEntry. The ZipEntry's name is set to the original file name, and its size is set
    using the Resource object's contentLength() method. The file content is then copied to the ZipOutputStream using
    StreamUtils.copy(). Finally, the ZipEntry is closed, and the ZipOutputStream is finished. If any exceptions occur
     during this process, the error message is printed to the console.*/
    @GetMapping("/zipDownload")
    void zipDownload(@RequestParam("fileName") String[] files, HttpServletResponse response) throws IOException {

        try(ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())){
            Arrays.stream(files)
                    .forEach(file -> {
                        Resource resource = null;
                        try {
                            resource = fileStorageService.downloadFile(file);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        assert resource != null;
                        ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(resource.getFilename()));

                        try {
                            zipEntry.setSize(resource.contentLength());
                            zos.putNextEntry(zipEntry);

                            StreamUtils.copy(resource.getInputStream(), zos);

                            zos.closeEntry();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            System.out.println("Some exception while zipping : " + e.getMessage());
                        }

                    });
            zos.finish();
        }

        response.setStatus(200);
        //response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=zipfile");
    }
}
