package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseStorageService {

    @Value("${supabase.project-ref}")
    private String projectRef;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.secret-key}") // Service Role Key をここにセット
    private String serviceRoleKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(byte[] fileBytes, String objectPath, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("apikey", serviceRoleKey);
        headers.setContentType(contentType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType));

        HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

        String uploadUrl = String.format(
                "https://%s.supabase.co/storage/v1/object/%s/%s",
                projectRef, bucket, objectPath
        );

        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Supabase upload failed: " + response.getStatusCode() + " " + response.getBody());
        }

        return String.format("https://%s.supabase.co/storage/v1/object/public/%s/%s",
                projectRef, bucket, objectPath);
    }
}
