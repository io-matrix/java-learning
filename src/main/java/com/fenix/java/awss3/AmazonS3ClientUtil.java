package com.fenix.java.awss3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AmazonS3ClientUtil {


    private static Integer MAX_TIME_OUT = 600000;

    public static AmazonS3 getAwsS3Client(String accessKey, String secretKey, String url) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setClientExecutionTimeout(MAX_TIME_OUT);
        clientConfiguration.setConnectionTimeout(MAX_TIME_OUT);
        clientConfiguration.setConnectionMaxIdleMillis(MAX_TIME_OUT);
        clientConfiguration.setSocketTimeout(MAX_TIME_OUT);


        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.US_EAST_1.getName()))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .build();
    }


    public static AmazonS3 getXskyS3Client(String accessKey, String secretKey, String url) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(url, Regions.DEFAULT_REGION.getName()))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    public static void cleanCache() {
//        LOCAL_S3.remove();
    }


}
