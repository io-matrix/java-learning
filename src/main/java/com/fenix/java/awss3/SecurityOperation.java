package com.fenix.java.awss3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class SecurityOperation {


    public static void main(String[] args) throws NoSuchAlgorithmException {
        testEncrypt2();
    }


    public static AmazonS3 getAwsS3Client(String accessKey, String secretKey, String url) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .withClientConfiguration(new ClientConfiguration().withSignerOverride("S3SignerType"))
                .withPathStyleAccessEnabled(true)
                .build();
    }


    public static void testEncrypt2() throws NoSuchAlgorithmException {

        String url = "https://ss-rgw-datalake-gdmm.datalake.cn:39443";
        String accessKey = "";
        String secretKey = "";
        String bucket_name = "test2022051203";

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);


//        generate a symmetric encryption key for testing
//         SecretKey secret = keyGenerator.generateKey();
        // 自定义秘钥
        String key = "afba7afbe1784697af418b86f58809c9";
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);

        // 生成secret
        SecretKey secret = new SecretKeySpec(bytes, "AES");

        System.out.println(secret.getFormat());
        System.out.println(secret.getAlgorithm());
        System.out.println(secret.getEncoded());
        String s3ObjectKey = "EncryptedContent3.txt";
        String s3ObjectContent = "This is the 2nd content to encrypt";

        // 生成加密客户端
        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .withCryptoConfiguration(new CryptoConfiguration().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secret)))
                .withPathStyleAccessEnabled(true)
                .build();

        // 加密上传
        //s3Encryption.putObject(bucket_name, s3ObjectKey, s3ObjectContent);
//        System.out.println(s3Encryption.getObjectAsString(bucket_name, s3ObjectKey));
        //s3Encryption.putObject(bucket_name, s3ObjectKey, s3ObjectContent);

        // 加密获取
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket_name, s3ObjectKey);
        ObjectMetadata object = s3Encryption.getObject(getObjectRequest, new File("/Users/feng/test.txt"));

        //System.out.println(s3Encryption.getObjectAsString(bucket_name, "1.png"));
        s3Encryption.shutdown();
    }



    public static void testEncrypt() {

        String url = "https://haerbin-woyun.datalake.cn:39443";
        String accessKey = "";
        String secretKey = "";
        String bucket_name = "aaasss";

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .build();

        // create KMS key for testing this example
        CreateKeyRequest createKeyRequest = new CreateKeyRequest();
        CreateKeyResult createKeyResult = kmsClient.createKey(createKeyRequest);

// --
        // specify an AWS KMS key ID
        String keyId = createKeyResult.getKeyMetadata().getKeyId();

        String s3ObjectKey = "EncryptedContent1.txt";
        String s3ObjectContent = "This is the 1st content to encrypt";
// --

        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .withCryptoConfiguration(new CryptoConfiguration().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterials(new KMSEncryptionMaterialsProvider(keyId))
                .build();

        //s3Encryption.putObject(bucket_name, s3ObjectKey, s3ObjectContent);
        System.out.println(s3Encryption.getObjectAsString(bucket_name, s3ObjectKey));

        // schedule deletion of KMS key generated for testing
        ScheduleKeyDeletionRequest scheduleKeyDeletionRequest =
                new ScheduleKeyDeletionRequest().withKeyId(keyId).withPendingWindowInDays(7);
        kmsClient.scheduleKeyDeletion(scheduleKeyDeletionRequest);

        s3Encryption.shutdown();
        kmsClient.shutdown();
    }


}

