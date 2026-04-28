package in.edu.kristujayanti.util;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class AWSS3Util {

    private static final Region REGION = Region.AP_SOUTH_1; // Change this to your preferred region
    private static S3AsyncClient s3Client;

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Util.class);

    private final Properties awsProperties;

    public AWSS3Util() {
        this.awsProperties = loadAwsProperties();
    }

    private Properties loadAwsProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("aws_config.properties")) {
            if (input == null) {
                LOGGER.error("Unable to find aws_config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Error loading aws properties", ex);
        }
        return properties;
    }

    public void initializeAWSS3() throws IOException {
        if (s3Client == null) {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                    this.awsProperties.getProperty("aws.accessKeyId"),
                    this.awsProperties.getProperty("aws.secretKey")
            );
//            s3Client = S3Client.builder()
//                    .region(REGION)
//                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
//                    .build();
//        }

            s3Client = S3AsyncClient.builder()
                    .region(REGION)
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .httpClient(NettyNioAsyncHttpClient.builder()
                            // Allow up to 200 concurrent connections
                                    .connectionTimeout(Duration.ofSeconds(10))
                                    .build()
                    )
                    .build();
        }
    }

//    public String uploadFileToS3(File file, String keyWithoutExtension) throws IOException {
//        initializeAWSS3(); // Initialize the S3 client
//        try {
//            // Get the file extension from the file name
//            String fileExtension = getFileExtension(file.getName());
//
//            // Ensure the key contains the extension
//            String key = keyWithoutExtension + (fileExtension.isEmpty() ? "" : "." + fileExtension);
//
//            // Create the PutObjectRequest
//            PutObjectRequest request = PutObjectRequest.builder()
//                    .bucket(this.awsProperties.getProperty("aws.bucketName"))
//                    .key(key)
//                    .build();
//
//            // Upload the file to S3
//            s3Client.putObject(request, Paths.get(file.getPath()));
//
//            // Return the S3 file URL
//            return s3Client.utilities().getUrl(builder -> builder.bucket(this.awsProperties.getProperty("aws.bucketName")).key(key)).toString();
//
//        } catch (Exception e) {
//            throw new IOException("Error uploading file to S3: " + e.getMessage(), e);
//        }
//    }

//    public CompletableFuture<String> uploadFileToS3(File file, String keyWithoutExtension) throws IOException {
//        initializeAWSS3(); // Initialize the S3 client
//
//        String fileExtension = getFileExtension(file.getName());
//        String key = keyWithoutExtension + (fileExtension.isEmpty() ? "" : "." + fileExtension);
//
//        PutObjectRequest request = PutObjectRequest.builder()
//                .bucket(this.awsProperties.getProperty("aws.bucketName"))
//                .key(key)
//                .contentType(determineContentType(file)) // Set content type
//                .build();
//
//        return s3Client.putObject(request, AsyncRequestBody.fromFile(file))
//                .thenApply(response -> {
//                    // Manually construct the file URL
//                    String bucketName = this.awsProperties.getProperty("aws.bucketName");
//                    String region = REGION.toString();
//                    return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
//                })
//                .exceptionally(ex -> {
//                    LOGGER.error("Error uploading file to S3: {}", ex.getMessage(), ex);
//                    return null; // Return null or handle it based on your needs
//                });
//    }

    public Future<String> uploadFileToS3(File file, String keyWithoutExtension) throws IOException {
        Promise<String> promise = Promise.promise();
        this.initializeAWSS3();

        String fileExtension = this.getFileExtension(file.getName());
        String key = keyWithoutExtension + (fileExtension.isEmpty() ? "" : "." + fileExtension);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(this.awsProperties.getProperty("aws.bucketName"))
                .key(key)
                .contentType(this.determineContentType(file))
                .build();

        s3Client.putObject(request, AsyncRequestBody.fromFile(file))
                .thenApply(response -> {
                    String bucketName = this.awsProperties.getProperty("aws.bucketName");
                    String region = REGION.toString();
                    String s3Url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
                    promise.complete(s3Url);
                    return s3Url;
                })
                .exceptionally(ex -> {
                    LOGGER.error("Error uploading file to S3: {}", ex.getMessage(), ex);
                    promise.fail("Error uploading file to S3: " + ex.getMessage());
                    return null;
                });

        return promise.future();
    }

    public String determineContentType(File file) {
        Tika tika = new Tika();
        try {
            return tika.detect(file);
        } catch (Exception e) {
            return "application/octet-stream"; // Default binary type
        }
    }



    // Helper method to extract the file extension from the file name
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex + 1);
    }

    public void deleteFileFromS3(String key) throws IOException {
        initializeAWSS3();
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(this.awsProperties.getProperty("aws.bucketName"))
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new IOException("Error deleting file from S3: " + e.getMessage(), e);
        }
    }

//    public InputStream downloadFileFromS3(String key) throws IOException {
//        initializeAWSS3();
//        try {
//            if (!doesObjectExist(key)) {
//                LOGGER.error("The specified file does not exist in S3 {}", key);
//            }
//            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                    .bucket(this.awsProperties.getProperty("aws.bucketName"))
//                    .key(key)
//                    .build();
//            return s3Client.getObject(getObjectRequest);
//        } catch (S3Exception e) {
//            LOGGER.error("Error downloading file from S3: {}", e.getMessage());
//            return InputStream.nullInputStream();
//        }
//    }

//    public CompletableFuture<ByteArrayInputStream> downloadFileFromS3(String key) throws IOException {
//        initializeAWSS3();
//
//        if (!doesObjectExist(key)) {
//            LOGGER.error("The specified file does not exist in S3 {}", key);
//            return CompletableFuture.completedFuture(new ByteArrayInputStream(new byte[0]));
//        }
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(this.awsProperties.getProperty("aws.bucketName"))
//                .key(key)
//                .build();
//
//        return s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
//                .thenApply(responseBytes -> new ByteArrayInputStream(responseBytes.asByteArray()))
//                .exceptionally(ex -> {
//                    LOGGER.error("Error downloading file from S3: {}", ex.getMessage());
//                    return new ByteArrayInputStream(new byte[0]); // Return empty stream on failure
//                });
//    }


    public Future<ByteArrayInputStream> downloadFileFromS3(String key) throws IOException {
        Promise<ByteArrayInputStream> promise = Promise.promise();
        initializeAWSS3();

        if (!doesObjectExist(key)) {
            LOGGER.error("The specified file does not exist in S3 {}", key);
            promise.complete(new ByteArrayInputStream(new byte[0])); // Return empty stream
            return promise.future();
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(this.awsProperties.getProperty("aws.bucketName"))
                .key(key)
                .build();

        s3Client.getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
                .thenApply(responseBytes -> new ByteArrayInputStream(responseBytes.asByteArray()))
                .thenAccept(promise::complete)
                .exceptionally(ex -> {
                    LOGGER.error("Error downloading file from S3: {}", ex.getMessage());
                    promise.fail("Error downloading file from S3: " + ex.getMessage());
                    return null;
                });

        return promise.future();
    }


    public boolean doesObjectExist(String key) throws IOException {
        initializeAWSS3();
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(this.awsProperties.getProperty("aws.bucketName"))
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
