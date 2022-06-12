package com.java.java17.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;

import java.util.List;

public class S3Demo {

    static String ENDPOINT = "";
    static String AK = "";
    static String SK = "";

    public static void main(String[] args) {
        testDeleteObject();
    }

    public static void testHeadObject() {
        String bucketName = "fenix";
        String key = "books/test.zip";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

        try {
            ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(bucketName, key);
            System.out.println(objectMetadata.getRawMetadata());
        } catch (AmazonS3Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testDeleteObject() {


        String bucketName = "fenix";
        String prefix = "books/";

        String continuationToken = "";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(bucketName);
            listObjectsV2Request.setPrefix(prefix);
            listObjectsV2Request.setDelimiter("/");
            listObjectsV2Request.setMaxKeys(40000);
            listObjectsV2Request.setContinuationToken(continuationToken);

            ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);

            List<String> commonPrefixes = listObjectsV2Result.getCommonPrefixes();

            for (String commonPrefix : commonPrefixes) {
                ListObjectsV2Result listObjects = awsS3Client.listObjectsV2(bucketName, commonPrefix);
                for (S3ObjectSummary objectSummary : listObjects.getObjectSummaries()) {
                    awsS3Client.deleteObject(bucketName, objectSummary.getKey());
                }
            }

            if (StringUtils.isNullOrEmpty(listObjectsV2Result.getContinuationToken())) {
                return;
            }
            continuationToken = listObjectsV2Result.getContinuationToken();
        }
    }
}
