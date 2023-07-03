package com.fenix.java.awss3;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.BlockPolicy;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class S3Operation {


    static Logger log = LoggerFactory.getLogger(S3Operation.class);

    static volatile int count = 0;

    static String ENDPOINT = "http://172.38.30.36:7480";
    static String AK = "F5TRYFKXCL2BUND5ELRW";
    static String SK = "RQ2ZdMujDsyEwPDeYWCCUIsUCmjRDCdnaimucGgI";

    static String XSKY_COUNT = "XSKY_COUNT";
    static String ENCODE_COUNT = "ENCODE_COUNT";
    static String ENCODE_KEYS = "ENCODE_KEYS";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) throws IOException {
        String bucketName = "bucket-test";

        awsS3Client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        System.out.println("设置成功");


//        listObjects("f0d81a-1628236738173", "");

//        headObject();

//        String bucket = "ykzxyy011596158064";
//
////        String prefix = "sync";
//        String prefix = "his/rman";
////        String prefix3 = "pacs";
//
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        new Thread(() -> sumSize(bucket, prefix, countDownLatch)).start();
////        new Thread(() -> sumSize(bucket, prefix2, countDownLatch)).start();
////        new Thread(() -> sumSize(bucket, prefix3, countDownLatch)).start();
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        log.info("end");


//        File file = new File("D:\\Downloads\\nohup.out");
//        long length = file.length();
//        log.info("{}", length);
//
//
//        String s1 = SecureUtil.md5(file);
//
////        String s = Md5Utils.md5AsBase64(file);
//        log.info("md5: {}", s1);

//        headObject();
    }

    /**
     * 容量格式化
     *
     * @param fileSize
     * @return
     */
    public static String formatFileSize(long fileSize) {

        DecimalFormat df = new DecimalFormat("#.00");

        if (fileSize < 1024) {
            return df.format((double) fileSize) + "B";
        }

        if (fileSize < 1048576) {
            return df.format((double) fileSize / 1024) + "K";
        }

        if (fileSize < 1073741824) {
            return df.format((double) fileSize / 1048576) + "M";
        }

        if (fileSize < 1099511627776L) {
            return df.format((double) fileSize / 1073741824) + "G";
        }

        return df.format((double) fileSize / 1099511627776L) + "T";
    }

    public static void changeStorageClass() {

        String bucket = "testnbu";

        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
        listObjectsV2Request.setBucketName(bucket);
        listObjectsV2Request.setMaxKeys(20000);


        ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
        int count = 0;
        for (S3ObjectSummary objectSummary : objectSummaries) {
            if (objectSummary.getKey().endsWith("/") || objectSummary.getStorageClass().equals("GLACIER")) {
                continue;
            }
            count++;
            awsS3Client.changeObjectStorageClass(bucket, objectSummary.getKey(), StorageClass.Glacier);
        }
        log.info("{}", count);


    }


    public static long sumSize(String bucket, String prefix, CountDownLatch countDownLatch) {


        long totalSize = 0;
        String continueToken = "";

        int successCount = 0;
        int failCount = 0;
        int copyCount = 0;
        List<String> keys = new ArrayList<>();
        List<S3ObjectSummary> list = new ArrayList<>();
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(bucket);
            listObjectsV2Request.setPrefix(prefix);
            listObjectsV2Request.setContinuationToken(continueToken);
            listObjectsV2Request.setMaxKeys(10000);


            ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);


            List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
            for (S3ObjectSummary objectSummary : objectSummaries) {
                keys.add(objectSummary.getKey());
                list.add(objectSummary);

                long size = objectSummary.getSize();
                totalSize += size;
            }

//            log.info("成功数：{}， 失败数：{}， 复制数：{}", successCount, failCount, copyCount);

            if (StrUtil.isEmpty(listObjectsV2Result.getNextContinuationToken())) {
                break;
            }
            continueToken = listObjectsV2Result.getNextContinuationToken();
        }
        StringBuilder result = new StringBuilder("[");
        for (String key : keys) {
            result.append("\"" + key + "\",");
        }
        result.append("]");
        log.info("{}", result.toString());

        log.info("{}", keys);

        List<S3ObjectSummary> collect = list.stream().sorted((o1, o2) -> o1.getLastModified().before(o2.getLastModified()) ? 1 : -1).collect(Collectors.toList());

        List<YingkouDate> yingkouDates = new ArrayList<>();
        for (S3ObjectSummary s3ObjectSummary : collect) {
            YingkouDate yingkouDate = new YingkouDate();
            yingkouDate.setKey(s3ObjectSummary.getKey());
            yingkouDate.setSize(formatFileSize(s3ObjectSummary.getSize()));
            yingkouDate.setDate(DateUtil.format(s3ObjectSummary.getLastModified(), DatePattern.NORM_DATETIME_PATTERN));
            yingkouDate.setBucket(bucket);
            yingkouDates.add(yingkouDate);
            log.info("key: {}; size: {}; date: {}", s3ObjectSummary.getKey(), formatFileSize(s3ObjectSummary.getSize()), DateUtil.format(s3ObjectSummary.getLastModified(), DatePattern.NORM_DATETIME_PATTERN));
        }

        // 导出Excel
        EasyExcel.write("./rman.xlsx", YingkouDate.class)
                .sheet()
                .doWrite(yingkouDates);

        log.info("{}: {}", prefix, formatFileSize(totalSize));
        countDownLatch.countDown();
        return totalSize;
    }

    public static void checkObject() {

        String srcBucket = "gam_cur-222-sy20210302";
        String destBucket = "gam-his-222-h";

        String srcPrefix = "sy20210302/";
        String destPrefix = "";

        String continueToken = "";

        int successCount = 0;
        int failCount = 0;
        int copyCount = 0;
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(srcBucket);
            listObjectsV2Request.setPrefix(srcPrefix);
            listObjectsV2Request.setContinuationToken(continueToken);
            listObjectsV2Request.setMaxKeys(1000);


            ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);

            List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
            for (S3ObjectSummary objectSummary : objectSummaries) {
                String destKey = destPrefix + objectSummary.getKey();
                ListObjectsV2Result destObjectsV2Result = awsS3Client.listObjectsV2(destBucket, destKey);
                List<S3ObjectSummary> destObjectSummaries = destObjectsV2Result.getObjectSummaries();

                if (destObjectSummaries.size() > 0) {
                    S3ObjectSummary s3ObjectSummary = destObjectSummaries.get(0);
                    if (s3ObjectSummary.getKey().equals(destKey) && objectSummary.getSize() == s3ObjectSummary.getSize() && objectSummary.getETag().equals(s3ObjectSummary.getETag())) {
                        try {
                            awsS3Client.deleteObject(srcBucket, objectSummary.getKey());
                            log.info("bucket={},key={} 已迁移，并成功删除源数据", srcBucket, objectSummary.getKey());
                            successCount++;
                        } catch (Exception e) {
                            log.error("{} == 删除失败", objectSummary.getKey());
                            log.error("删除 error: ", e);
                            failCount++;
                        }
                    }
                } else {
                    try {
                        awsS3Client.copyObject(srcBucket, objectSummary.getKey(), destBucket, destKey);
                        log.info("复制成功 {}", objectSummary.getKey());
                        copyCount++;
                    } catch (Exception e) {
                        log.error("{} == 复制失败", objectSummary.getKey());
                        log.error("复制 error: ", e);
                    }
                }
            }

            log.info("成功数：{}， 失败数：{}， 复制数：{}", successCount, failCount, copyCount);

            if (StrUtil.isEmpty(listObjectsV2Result.getNextContinuationToken())) {
                break;
            }
            continueToken = listObjectsV2Result.getNextContinuationToken();
        }
    }

    public static void moveData() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                12,
                12,
                0L,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(60),
                new BlockPolicy()
        );

        String srcBucket = "gam_currentdata";
        String srcPath = "sy20190415/";
        String destBucket = "gam-his-222-g";
        String destPath = "";


        String startAfter = "sy20190415/07072020/Yang Hai Yan/C2014732/1.3.12.2.1107.5.1.4.80456.30000020070700043710400016926";

//        String srcBucket = "fenix";
//        String srcPath = "sync2/";
//        String destBucket = "fenix";
//        String destPath = "move/";


        String continueToken = "";
        while (true) {
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
            listObjectsV2Request.setBucketName(srcBucket);
            listObjectsV2Request.setPrefix(srcPath);
            listObjectsV2Request.setStartAfter(startAfter);
            listObjectsV2Request.setContinuationToken(continueToken);
            listObjectsV2Request.setMaxKeys(100);
            ListObjectsV2Result listObjectsV2Result = null;
            try {
                listObjectsV2Result = awsS3Client.listObjectsV2(listObjectsV2Request);
            } catch (Exception e) {
                log.error("查询异常：", e);
                continue;
            }

            List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

            for (S3ObjectSummary objectSummary : objectSummaries) {
                count += 1;

                log.info("{}. 迁移开始 ： {}", count, objectSummary.getKey());
                int finalCount = count;
                executor.execute(() -> {
                    final Logger successLog = LoggerFactory.getLogger("successLog");
                    final Logger failLog = LoggerFactory.getLogger("failLog");
                    String destKey = destPath + objectSummary.getKey();
                    try {
                        CopyObjectResult copyObjectResult = awsS3Client.copyObject(srcBucket, objectSummary.getKey(), destBucket, destKey);
                        if (!copyObjectResult.getETag().equals(objectSummary.getETag())) {
                            failLog.info(objectSummary.getKey());
                        } else {
                            successLog.info(objectSummary.getKey());
                        }
                    } catch (Exception e) {
                        log.error("{} 迁移 error:", objectSummary.getKey(), e);
                        failLog.info(objectSummary.getKey());
                    }

                    log.info("{}. 迁移完成 ： {}", finalCount, objectSummary.getKey());
                });

            }

            if (StrUtil.isEmpty(listObjectsV2Result.getNextContinuationToken())) {
                break;
            }
            continueToken = listObjectsV2Result.getNextContinuationToken();
        }

        executor.shutdown();
        while (!executor.isShutdown()) {
            try {
                log.info("总任务数： {}， 已完成任务数： {}", count, executor.getCompletedTaskCount());
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error("error: ", e);
            }
        }

    }

    /**
     * 迁移失败日志中的key
     */
    public static void moveSingleObj() {
        String srcBucket = System.getProperty("SRC_BUCKET");
        String destBucket = System.getProperty("DEST_BUCKET");
        String filePath = System.getProperty("FILE_PATH");
        List<String> keys = FileUtil.readUtf8Lines(filePath);
        int count = 0;
        for (String key : keys) {
            count++;
            try {
                awsS3Client.copyObject(srcBucket, key, destBucket, key);
                log.info("{}. {} 迁移完成", count, key);
            } catch (Exception e) {
                log.error("error: {} ", key, e);
            }
        }
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

        List<Bucket> buckets = awsS3Client.listBuckets();

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
        String bucket = "gam-his-222-f";
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

            if (StringUtils.isNullOrEmpty(listObjectsV2Result.getNextContinuationToken())) {
                break;
            }
            continuationToken = listObjectsV2Result.getNextContinuationToken();
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
        String bucketName = "test-0926";
        String key = "test.log3";

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
