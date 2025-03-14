package com.usermgmt.usermgmtsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * Uploads a file and returns its URL or identifier.
     *
     * @param file The file to be uploaded.
     * @return A string representing the uploaded file's location.
     */
    String uploadFile(MultipartFile file);
}