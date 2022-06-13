package com.java.java17.awss3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class S3Demo {

    static String ENDPOINT = "http://192.168.5.5:8060";
    static String AK = "X3VQEBXL93IU1XG6XU6Z";
    static String SK = "jnajALyYJn3IBC70XknNuntpJBuQdnqtDkVfIJS6";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        testListUploads();
    }


    /**
     * 统计分片上传大小
     */
    public static void testListUploads() {


        String bucket = "rlzysclipy1623229510";

        long totalSize = 0;

        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucket);
        listMultipartUploadsRequest.setMaxUploads(30000);
        MultipartUploadListing multipartUploadListing = awsS3Client.listMultipartUploads(listMultipartUploadsRequest);
        log.info("upload 个数： " + multipartUploadListing.getMultipartUploads().size());
        for (MultipartUpload multipartUpload : multipartUploadListing.getMultipartUploads()) {
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucket, multipartUpload.getKey(), multipartUpload.getUploadId());

            PartListing partListing = awsS3Client.listParts(listPartsRequest);
            for (PartSummary part : partListing.getParts()) {
                log.info("part size: " + part.getSize());
                totalSize += part.getSize();
            }
        }

        log.info("总大小：{}", totalSize);
    }

    /**
     * 获取对象meta信息
     */
    public static void testHeadObject() {
        String bucketName = "fenix";
        String key = "books/test.zip";

        try {
            ObjectMetadata objectMetadata = awsS3Client.getObjectMetadata(bucketName, key);
            log.info(objectMetadata.getRawMetadata().toString());
        } catch (AmazonS3Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 删除文件夹目录中对象
     */
    public static void testDeleteObject() {


        String bucketName = "fenix";
        String prefix = "books/";

        String continuationToken = "";
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
