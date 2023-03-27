package com.jathursh.file_upload_download.service;

import com.jathursh.file_upload_download.dto.FileDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocFileDao extends CrudRepository<FileDocument, Long> {

    FileDocument findByFileName(String fileName);
}
