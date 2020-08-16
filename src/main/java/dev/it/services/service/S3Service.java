package dev.it.services.service;

import dev.it.services.model.pojo.FormData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@Singleton
public class S3Service {

    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Inject
    S3Client s3;

    @ConfigProperty(name = "bucket.name")
    String bucketName;

    public S3Service() {
    }

    protected PutObjectRequest buildPutRequest(FormData formData, String uuid) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uuid)
                .contentType(formData.mimeType)
                .build();
    }

    protected GetObjectRequest buildGetRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    protected DeleteObjectRequest buildDeleteRequest(String objectKey) {
        return DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    protected File tempFilePath() {
        return new File(TEMP_DIR, new StringBuilder().append("s3AsyncDownloadedTemp")
                .append((new Date()).getTime()).append(UUID.randomUUID())
                .append(".").append(".tmp").toString());
    }

    protected File uploadToTemp(InputStream data) {
        File tempPath;
        try {
            tempPath = File.createTempFile("uploadS3Tmp", ".tmp");
            Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return tempPath;
    }

    public void deleteObject(String uuid) {
        s3.deleteObject(buildDeleteRequest(uuid));
    }


    public ByteArrayOutputStream downloadObject(String uuid) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        s3.getObject(buildGetRequest(uuid), ResponseTransformer.toOutputStream(baos));
        return baos;
    }

    public PutObjectResponse uploadObject(FormData formData, String uuid) {
        File fileTmp = uploadToTemp(formData.data);
//        document.size = FileUtils.byteCountToDisplaySize(fileTmp.length());
        PutObjectResponse putResponse = s3.putObject(buildPutRequest(formData, uuid),
                RequestBody.fromFile(fileTmp));
        return putResponse;
    }
}
