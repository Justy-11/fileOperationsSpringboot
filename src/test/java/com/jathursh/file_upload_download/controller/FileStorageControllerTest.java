package com.jathursh.file_upload_download.controller;

import com.jathursh.file_upload_download.service.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // replace a real bean with a mock object that behaves in a certain way
    private FileStorageService fileStorageService;

    @Test
    @DisplayName("test to upload single file")
    void shouldUploadSingleFile() throws Exception {

        Mockito.when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("test-file.txt");

        MockMultipartFile multipartFile = new MockMultipartFile("file","test-file.txt",
                "text/plain", "Jathursh".getBytes());

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/single/upload").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fileName\":test-file.txt,\"contentType\":\"text/plain\",\"url\":\"http://localhost/download/test-file.txt\"}"));


        //verify whether fileStorageService calls storeFile() or not
        then(this.fileStorageService).should().storeFile(multipartFile);
        // below line is going to fail because fileStorageService never called downloadFile() above - just for ex:
        then(this.fileStorageService).should().downloadFile("dummy");
    }

}