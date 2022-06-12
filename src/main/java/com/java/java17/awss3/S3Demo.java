package com.java.java17.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class S3Demo {

    public static void main(String[] args) {
        testHeadObject();
    }

    public static void testHeadObject() {


        String endpoint = "";
        String ak = "";
        String sk = "";

        String bucketName = "fenix";
        String key = "books/test.zip";

        AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(ak, sk, endpoint);

        try {
            ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(bucketName, key);
            System.out.println(objectMetadata.getRawMetadata());
        } catch (AmazonS3Exception e) {
            System.out.println(e.getMessage());
        }


    }

}
