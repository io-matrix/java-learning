package com.fenix.java.awss3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public static void testPutObject() {
        String url = "https://haerbin-woyun.datalake.cn:39443";
        String accessKey = "";
        String secretKey = "";
        String bucket_name = "aaasss";


        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(accessKey, secretKey, url);

        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            key.append("a");
        }

        PutObjectResult putObjectResult = awsS3Client.putObject(bucket_name, key.toString(), new File("D:\\Feng\\Pictures\\92fqkco8lde51.png"));

        System.out.println(putObjectResult.getMetadata());
        System.out.println(putObjectResult.getETag());
    }


    public static void testGdasHead() {
        String url = "https://haerbin-woyun.datalake.cn:39000";
        String ak = "";
        String sk = "";

        String bucket = "886b46-1621256971926";

        String key = "717ce6c5-f590-4059-8283-86f49b192cb0.51025778.1/717ce6c5-f590-4059-8283-86f49b192cb0.51025778.14415";


        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(ak, sk, url);

        ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(bucket, key);


        System.out.println(objectMetadata.getETag());

        ObjectListing objectListing = awsS3Client.listObjects(bucket);

        System.out.println(objectListing.getObjectSummaries());

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

        String key = "afba7afbe1784697af418b86f58809c9";
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);

        SecretKey secret = new SecretKeySpec(bytes, "AES");

        System.out.println(secret.getFormat());
        System.out.println(secret.getAlgorithm());
        System.out.println(secret.getEncoded());
        String s3ObjectKey = "EncryptedContent3.txt";
        String s3ObjectContent = "This is the 2nd content to encrypt";

        AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .withCryptoConfiguration(new CryptoConfiguration().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secret)))
                .withPathStyleAccessEnabled(true)
                .build();


        //s3Encryption.putObject(bucket_name, s3ObjectKey, s3ObjectContent);
//        System.out.println(s3Encryption.getObjectAsString(bucket_name, s3ObjectKey));
        //s3Encryption.putObject(bucket_name, s3ObjectKey, s3ObjectContent);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket_name, s3ObjectKey);
        ObjectMetadata object = s3Encryption.getObject(getObjectRequest, new File("/Users/feng/test.txt"));

        //System.out.println(s3Encryption.getObjectAsString(bucket_name, "1.png"));
        s3Encryption.shutdown();
    }


    public static void testCopyPart() {


        String url = "https://ss-rgw-datalake-gdmm.datalake.cn:39443";
        String ak = "";
        String sk = "";

        String sourceBucketName = "buc";
        String sourceObjectKey = "25e8b49308d4424ea9aa1e75a6225c4.png";
        String destBucketName = "test20220425";
        String destObjectKey = "25e8b49308d4424ea9aa1e75a6225c4.png";

        try {

            AmazonS3 s3Client = AmazonS3ClientUtil.getAwsS3Client(ak, sk, url);

            // Initiate the multipart upload.初始化复制任务
            InitiateMultipartUploadRequest initRequest =
                    new InitiateMultipartUploadRequest(destBucketName, destObjectKey);
            InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(initRequest);

            // Get the object size to track the end of the copy operation.获取文件大小
            GetObjectMetadataRequest metadataRequest =
                    new GetObjectMetadataRequest(sourceBucketName, sourceObjectKey);
            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength();

            // Copy the object using 5 GB parts.使用5GB块进行分块，java中long乘法一定要加L
            long partSize = 50L * 1024L;
            long bytePosition = 0L;
            int partNum = 1;// 分块只能从2号开始，不能从1号块
            List<PartETag> etags = new ArrayList<>();
            long startSUM = 0L;
            while (bytePosition < objectSize) {
                // The last part might be smaller than partSize, so check to make sure
                // that lastByte isn't beyond the end of the object.
                long lastByte = Math.min(bytePosition + partSize - 1, objectSize - 1);

                // Copy this part.
                CopyPartRequest copyRequest =
                        new CopyPartRequest()
                                .withSourceBucketName(sourceBucketName)
                                .withSourceKey(sourceObjectKey)
                                .withDestinationBucketName(destBucketName)
                                .withDestinationKey(destObjectKey)
                                .withUploadId(initResult.getUploadId())
                                .withFirstByte(bytePosition)
                                .withLastByte(lastByte)
                                .withPartNumber(partNum++);
                long start = System.currentTimeMillis();
                if (2 == partNum) {
                    startSUM = start;
                }
                CopyPartResult response = s3Client.copyPart(copyRequest);
                System.out.printf("第%d块复制消耗: %d ms", partNum, System.currentTimeMillis() - start);
                etags.add(new PartETag(response.getPartNumber(), response.getETag()));
                bytePosition += partSize;
            }

            // Complete the upload request to concatenate all uploaded parts and make the copied object
            // available.
            CompleteMultipartUploadRequest completeRequest =
                    new CompleteMultipartUploadRequest(
                            destBucketName, destObjectKey, initResult.getUploadId(), etags);
            s3Client.completeMultipartUpload(completeRequest);
            System.out.printf("总消耗: %d ms", System.currentTimeMillis() - startSUM);
        } catch (SdkClientException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }


    }

    public static void testCopy() {

        String url = "https://ss-rgw-datalake-gdmm.datalake.cn:39443";
        String ak = "";
        String sk = "";

        String srcbucket = "buc";

        String dstbucket = "test20220425";

        String key = "9-28用例评审.txt";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(ak, sk, url);


        CopyObjectResult copyObjectResult = awsS3Client.copyObject(srcbucket, key, dstbucket, key);
        System.out.println(copyObjectResult.getETag());


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

        // create KMS key for for testing this example
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

    public static void testlistVersion() {
        String url = "https://wx.ebrs-guiyang-1.cmecloud.cn:9443";
        String accessKey = "";
        String secretKey = "";
        String bucketName = "gl001";
        String key = "hhh/";
        int maxResults = 10;


        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(accessKey, secretKey, url);


        ListVersionsRequest listVersionsRequest = new ListVersionsRequest();
        listVersionsRequest.setBucketName(bucketName);
        listVersionsRequest.setPrefix(key);


        listVersionsRequest.setEncodingType("url");
        VersionListing versionListing = awsS3Client.listVersions(listVersionsRequest);
        for (S3VersionSummary versionSummary : versionListing.getVersionSummaries()) {
            System.out.println(versionSummary.getBucketName());
        }


        System.out.println("end");

    }


    public static void testMeta() {
        String url = "http://172.38.80.35:8080";
        String accessKey = "";
        String secretKey = "";
        String bucketName = "blue";
        String key = "key.txt";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(accessKey, secretKey, url);
        long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(bucketName, key);
        System.out.println(objectMetadata.getETag());
        long end = System.currentTimeMillis();
        System.out.println("end: " + end);
        System.out.println(end - start);

        System.out.println("end");

    }

    public static void testPresign() {
        String url = "http://[3ffe:ffff:7654:feda:1245:ba98:3210:4562]:8060";
        String accessKey = "";
        String secretKey = "";
        String bucketName = "gl001";
        String key = "data.bat";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(accessKey, secretKey, url);
        long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        URL url1 = awsS3Client.generatePresignedUrl(bucketName, key, new Date(System.currentTimeMillis() + 3600 * 1000));
        System.out.println("**");
        System.out.println(url1.toString());
        System.out.println("**");
        long end = System.currentTimeMillis();
        System.out.println("end: " + end);
        System.out.println(end - start);

        System.out.println("end");

    }

}

