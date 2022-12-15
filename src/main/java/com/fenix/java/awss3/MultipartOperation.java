package com.fenix.java.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * @author feng
 * @desc 描述
 * @date 2022/11/28 09:31
 * @since v1
 */
public class MultipartOperation {


    static String ENDPOINT = "https://haerbin-woyun.datalake.cn:39443";
    static String AK = "";
    static String SK = "";


    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);


    public static void main(String[] args) {
        testhead();

    }


    public static void testhead() {
        AmazonS3 s3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);
        ObjectMetadata objectMetadata = s3Client.getObjectMetadata("e7fe1f-1648464152630", "e7fe1f-1648464152630_c939f6278ba7519927b95f21e957d28c_47-2277");
        System.out.println("end");
    }
}
