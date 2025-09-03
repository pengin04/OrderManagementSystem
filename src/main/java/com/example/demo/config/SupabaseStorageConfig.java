package com.example.demo.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@ConfigurationProperties(prefix = "supabase")
public class SupabaseStorageConfig {

    private String projectRef;
    private String region;
    private String secretKey;

    public String getProjectRef() { return projectRef; }
    public void setProjectRef(String projectRef) { this.projectRef = projectRef; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    @Bean
    public S3Client supabaseS3() {
        AwsCredentials creds = AwsBasicCredentials.create("dummy", secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create("https://" + projectRef + ".supabase.co/storage/v1"))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of(region))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build())
                .build();
    }
}
