package com.jathursh.file_upload_download.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

// 27/3
@Controller
public class FileUploadWithFECode {

    @GetMapping("/files")
    ModelAndView fileUpload(){
        return new ModelAndView("index.html");
    }
}
