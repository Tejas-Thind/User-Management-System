package com.usermgmt.usermgmtsystem.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody; // This is the missing import

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service // Marks this class as a Spring service
public class AWSS3Service implements FileService {

    private final S3Client s3Client; // S3Client is used to interact with AWS S3

    // S3Client will be injected by Spring at runtime
    @Autowired
    public AWSS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override // Override the method from the FileService interface
    public String uploadFile(MultipartFile file) {
        try {
            // Extract the file extension from the original filename
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            // Generate a unique key for the file using UUID and appending the file
            // extension
            String key = UUID.randomUUID().toString() + "." + extension;

            // Create a map to hold metadata about the file
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType()); // Store the content type
            metadata.put("original-filename", file.getOriginalFilename()); // Store the original filename

            // Create a PutObjectRequest to specify the details of the file upload to S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("springthind4")
                    .key(key)
                    .metadata(metadata)
                    .build(); // Build the PutObjectRequest object

            // Upload the file to S3 using the S3Client, passing the request and the file
            // input stream
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()) // Provide the file's input
                                                                                       // stream and size
            );

            // Return the public URL of the uploaded file
            // (parameters) -> { body }
            return s3Client.utilities().getUrl(r -> r.bucket("springthind4").key(key)).toExternalForm();

        } catch (IOException e) { // Catch any IOExceptions
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while uploading file", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", e);
        }
    }
}
