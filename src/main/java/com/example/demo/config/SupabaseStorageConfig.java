package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class SupabaseStorageConfig {

    @Value("${supabase.project-ref}")
    private String projectRef;

    @Value("${supabase.region}")
    private String region;

    @Value("${supabase.access-key}")
    private String accessKey;

   @Value("${supabase.secret-key}")  // ← これに合わせる
private String secretKey;

    @Bean
    public S3Client supabaseS3() {
        AwsCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create("https://" + projectRef + ".supabase.co/storage/v1")) // ★ここを確認
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of(region))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true) // ★これを忘れずに！
                                .build())
                .build();
    }
}
