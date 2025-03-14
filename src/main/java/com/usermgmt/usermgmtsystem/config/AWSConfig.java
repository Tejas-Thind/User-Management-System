package com.usermgmt.usermgmtsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration // Marks this class as a Spring configuration component
public class AWSConfig {

    // Inject AWS credentials and region from application properties
    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    /**
     * Configures and provides an S3Client bean for interacting with AWS S3.
     */
    @Bean
    public S3Client s3Client() {
        // Create AWS credentials using access key and secret key
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);

        // Build and return an S3Client with the specified credentials and region
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of(region))
                .build();
    }
}
