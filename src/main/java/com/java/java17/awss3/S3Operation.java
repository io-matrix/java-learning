package com.java.java17.awss3;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class S3Operation {

    static String ENDPOINT = "http://5.5";
    static String AK = "2AF488DNKPO25F12N6E0";
    static String SK = "3pVCzgpEUVbik612aayAq7u5OP7uYuqSACPulKPh";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        testDeleteObject();
    }

    public static void countExcelObject() {
        String bucket = "";
        String readExcelPath = "";
        String writeExcelPath = "";
        final List<ExcelData> dataList = new ArrayList<>();
        EasyExcel.read(readExcelPath, ExcelData.class, new AnalysisEventListener<ExcelData>() {
            @Override
            public void invoke(ExcelData excelData, AnalysisContext analysisContext) {
                dataList.add(excelData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).doReadAll();

        for (ExcelData excelData : dataList) {
            long count = countObject(bucket, excelData.getPath());
            excelData.setXskyCount(String.valueOf(count));
        }

        EasyExcel.write(writeExcelPath, ExcelData.class)
                .sheet()
                .doWrite(dataList);

    }

    public static long countObject(String bucket, String prefix) {
        long count = 0;

        String continueToken = "";
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(bucket);
            listObjectsV2Request.setPrefix(prefix);
            listObjectsV2Request.setMaxKeys(10000);
            listObjectsV2Request.setContinuationToken(continueToken);
            ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);
            count += listObjectsV2Result.getKeyCount();
            if (StrUtil.isNotEmpty(listObjectsV2Result.getContinuationToken())) {
                continueToken = listObjectsV2Result.getContinuationToken();
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 统计分片上传大小
     */
    public static void listUploads() {
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
    public static void headObject() {
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
        String bucketName = "gam_currentdata";
        String prefix = "sy20160831/";

        String[] secPrefixs = {
                "01012017",
                "01012018",
                "01012019",
                "01022017",
                "01022018",
        };

        String continuationToken = "";

        for (int i = 0; i < secPrefixs.length; i++) {
            String secPrefix = secPrefixs[0];

            String finalPrefix = prefix + secPrefix + "/";

            while (true) {
                ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
                listObjectsV2Request.setBucketName(bucketName);
                listObjectsV2Request.setPrefix(finalPrefix);
                listObjectsV2Request.setDelimiter("/");
                listObjectsV2Request.setMaxKeys(10000);
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
                    break;
                }
                continuationToken = listObjectsV2Result.getContinuationToken();
            }
        }


    }
}
