package com.jathursh.file_upload_download.dto;

import jakarta.persistence.*;

@Entity
public class FileDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "docfile")
    @Lob  // Large Object (LOB) type, which can hold large amounts of data such as binary data, text, or images. In this case, docFile is a byte array that will store the binary data of a file.
    private byte[] docFile;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getDocFile() {
        return docFile;
    }

    public void setDocFile(byte[] docFile) {
        this.docFile = docFile;
    }
}
