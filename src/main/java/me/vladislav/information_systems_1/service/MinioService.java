package me.vladislav.information_systems_1.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import me.vladislav.information_systems_1.exception.MinioInitializationException;
import me.vladislav.information_systems_1.exception.MinioUploadFileException;

import java.io.InputStream;


@ApplicationScoped
public class MinioService {
    private static final String BUCKET = "imports";

    private MinioClient minioClient;

    @PostConstruct
    void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9002")
                    .credentials(
                            "vladislavmedvedev",
                            "123123123"
                    )
                    .build();

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            }
        } catch (Exception e) {
            throw new MinioInitializationException("Error initializing Minio", e);
        }
    }

    public void uploadFile(InputStream fileStream, String fileName) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(fileName + ".txt")
                    .stream(fileStream, fileStream.available(), -1)
                    .build()
            );
        } catch (Exception e) {
            throw new MinioUploadFileException("File upload error", e);
        }
    }

}
