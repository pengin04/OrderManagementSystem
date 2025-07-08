@Configuration
public class SupabaseStorageConfig {

    @Value("${supabase.project-ref}")
    private String projectRef;
    @Value("${supabase.region}")
    private String region;
    @Value("${supabase.access-key}")
    private String accessKey;
    @Value("${supabase.secret-key}")
    private String secretKey;

    @Bean
    public S3Client supabaseS3() {
        AwsCredentials creds =
            AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                // 例: https://abcd1234.supabase.co/storage/v1/s3
                .endpointOverride(URI.create(
                     "https://" + projectRef + ".supabase.co/storage/v1/s3"))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of(region))
                // S3 path‐style アクセス必須
                .serviceConfiguration(
                     S3Configuration.builder()
                       .pathStyleAccessEnabled(true).build())
                .build();
    }
}
