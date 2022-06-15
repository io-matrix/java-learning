package com.java.java17.awss3;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class S3Operation {

    static String ENDPOINT = "";
    static String AK = "2AF488DNKPO25F12N6E0";
    static String SK = "3pVCzgpEUVbik612aayAq7u5OP7uYuqSACPulKPh";

    static String XSKY_COUNT = "XSKY_COUNT";
    static String ENCODE_COUNT = "ENCODE_COUNT";

    static String ENCODE_KEYS = "ENCODE_KEYS";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        countExcelObject();
    }


    public static void deleteEncodeKeys() {
        int count = 0;
        List<String> keys = FileUtil.readUtf8Lines("D:\\code\\java-learning\\out\\artifacts\\java_learning_jar\\encodekeys.log");
        String bucket = "gam_currentdata";
        for (String key : keys) {
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            awsS3Client.deleteObject(bucket, key);
            count++;
        }
        log.info("成功删除 {} 个", count);
    }

    /**
     * 获取目录列表中的各目录下的 对象列表
     */
    public static void listObjKey() {
        String bucket = "gam_currentdata";
        String result = "";
        List<String> prefixs = Arrays.asList(
                "sy20160831/01022017/",
                "sy20160831/01022018/",
                "sy20160831/01152018/",
                "sy20160831/01162018/",
                "sy20160831/01172018/",
                "sy20160831/01192018/",
                "sy20160831/01202017/",
                "sy20160831/01202019/",
                "sy20160831/01212017/",
                "sy20160831/04242017/",
                "sy20160831/05252018/"
        );

        for (String prefix : prefixs) {
            String keys = listObjects(bucket, prefix);
            result = result + "\n" + keys + "\n************************************";
        }

        FileUtil.writeString(result, new File("D:\\data\\lesskeys.log"), StandardCharsets.UTF_8);

    }

    /**
     * 查询 以 .zip 为结尾的所有文件
     *
     * @param bucket
     * @param prefix
     * @return
     */
    public static String listObjects(String bucket, String prefix) {

        List<String> objKeys = new ArrayList<>();
        ObjectListing objectListing = awsS3Client.listObjects(bucket, prefix);
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            if (key.endsWith(".zip")) {
                objKeys.add(key);
            }
        }
        String join = StrUtil.join("\n", objKeys);

        return prefix + ":\n" + join;
    }

    /**
     * 查询excel中对应目录的对象数
     */
    public static void countExcelObject() {
        String bucket = "gam_currentdata";
        String readExcelPath = "D:\\data\\src.xlsx";
        String writeExcelPath = "D:\\data\\dest.xlsx";
        final List<ExcelData> dataList = new ArrayList<>();
        List<String> encodeKeyList = new ArrayList<>();
        EasyExcel.read(readExcelPath, ExcelData.class, new AnalysisEventListener<ExcelData>() {
            @Override
            public void invoke(ExcelData excelData, AnalysisContext analysisContext) {
                dataList.add(excelData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).doReadAll();

        log.info("excel 供 {} 条数据", dataList.size());

        for (ExcelData excelData : dataList) {
            if (StrUtil.isEmpty(excelData.getPath())) {
                break;
            }
            log.info(JSON.toJSONString(excelData));
            Map<String, Object> map = countObject(bucket, excelData.getPath());
            log.info("对象数 {}", map);
            excelData.setXskyCount((Long) map.get(XSKY_COUNT));
            excelData.setEncodeCodeCount((Long) map.get(ENCODE_COUNT));
            excelData.setDiffCount(excelData.getXskyCount() - excelData.getCount());
            encodeKeyList.add((String) map.get(ENCODE_KEYS));
        }

        String encodeKey = StrUtil.join("\n******************************\n", encodeKeyList);

        FileUtil.writeString(encodeKey, new File("D:\\data\\encodekeys.log"), StandardCharsets.UTF_8);

        EasyExcel.write(writeExcelPath, ExcelData.class)
                .sheet()
                .doWrite(dataList);

    }

    public static Map<String, Object> countObject(String bucket, String prefix) {
        long count = 0;
        long encodeCount = 0;
        List<String> encodeKeyList = new ArrayList<>();
        String continuationToken = "";
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(bucket);
            listObjectsV2Request.setPrefix(prefix);
            listObjectsV2Request.setMaxKeys(10000);
            listObjectsV2Request.setContinuationToken(continuationToken);
            ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);

            for (S3ObjectSummary objectSummary : listObjectsV2Result.getObjectSummaries()) {
                if (objectSummary.getKey().endsWith(".zip")) {
                    count += 1;
                }
                if (objectSummary.getKey().contains("%")) {
                    encodeCount += 1;
                    encodeKeyList.add(objectSummary.getKey());
                }
            }

            if (StringUtils.isNullOrEmpty(listObjectsV2Result.getContinuationToken())) {
                break;
            }
            continuationToken = listObjectsV2Result.getContinuationToken();
        }

        String join = StrUtil.join("\n", encodeKeyList);

        HashMap<String, Object> result = new HashMap<>();
        result.put(XSKY_COUNT, count);
        result.put(ENCODE_COUNT, encodeCount);
        result.put(ENCODE_KEYS, join);
        return result;
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
    public static void deleteObject() {
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
