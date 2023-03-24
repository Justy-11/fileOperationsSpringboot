package com.jathursh.file_upload_download.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;

@Service
public class FileStorageService {

    private Path fileStoragePath;
    private String fileStorageLocation;

    public FileStorageService(@Value("${file.storage.location=temp}") String fileStorageLocation) {

        this.fileStorageLocation = fileStorageLocation;
        fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        /*The Paths.get(fileStorageLocation) method is used to create a Path object from the fileStorageLocation string, which represents the path to a file or directory in the file system.
        The toAbsolutePath() method is then called on this Path object to convert it to an absolute path. An absolute path is a complete path that starts from the root directory of the file system. This is necessary because the original fileStorageLocation string may have been a relative path, which would be interpreted relative to the current working directory.
        Finally, the normalize() method is called on the Path object to normalize the path by removing redundant elements such as "." (current directory) and ".." (parent directory) components.*/

        try {
            Files.createDirectories(fileStoragePath);  // For example, if the specified path is "/path/to/new/directory", and "/path/to" does not exist, then this method will create those parent directories as well.
        } catch (IOException e) {
            //e.printStackTrace();``
            throw new RuntimeException("Issue in creating file directory");
        }
    }

    public String storeFile(MultipartFile file) {

        /*
        This method takes a MultipartFile object (representing an uploaded file) as its input and stores it in a specified location in the file system.
        First, it extracts the original filename from the MultipartFile object using the getOriginalFilename() method and cleans the filename using StringUtils.cleanPath() method. The cleanPath() method ensures that the filename is safe for use as a file system path by removing any potential path traversal characters or sequences.
        Next, it creates a Path object representing the file storage location by concatenating the fileStoragePath (which should be the directory where the files will be stored) with the cleaned filename using the Paths.get() method.
        Finally, it copies the contents of the uploaded file to the specified file path using the Files.copy() method, with the StandardCopyOption.REPLACE_EXISTING option to replace any existing file with the same name. If there is an error during the copy process, a RuntimeException is thrown.*/

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Paths.get(fileStoragePath + "\\" + fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Issue in storing the file");
        }
        return fileName;
    }

    public UrlResource downloadFile(String fileName) {

        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);

        //Resource resource;
        UrlResource urlResource;

        try {
            urlResource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in reading the file ", e);
        }

        if(urlResource.exists() && urlResource.isReadable()){
            return urlResource;
        }else{
            throw new RuntimeException("the file doesn't exist or not readable");
        }
    }
}
