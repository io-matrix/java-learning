package com.fenix.java.awss3;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateParser;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.StorageClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author feng
 * @desc 描述
 * @date 2022/11/23 13:39
 * @since v1
 */
@Slf4j
public class LIfeCycleOperation {


    static String ENDPOINT = "http://172.38.30.194:7480";
    static String AK = "";
    static String SK = "";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        setLifecycle();
//        listLifeCycle();
    }

    public static void listLifeCycle() {

        BucketLifecycleConfiguration bucketLifecycleConfiguration = awsS3Client.getBucketLifecycleConfiguration("test-yys");
        List<BucketLifecycleConfiguration.Rule> rules = bucketLifecycleConfiguration.getRules();
        log.info(JSON.toJSONString(bucketLifecycleConfiguration));
        for (BucketLifecycleConfiguration.Rule rule : rules) {
            log.info("{}", JSON.toJSONString(rule));
        }


    }

    public static void setLifecycle() {

        BucketLifecycleConfiguration bucketLifecycleConfiguration = awsS3Client.getBucketLifecycleConfiguration("test-yys");
        List<BucketLifecycleConfiguration.Rule> rules = bucketLifecycleConfiguration.getRules();

        BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule();
        rule.setId("test-p");
        rule.setPrefix("p/");
        rule.setExpirationInDays(-1);
        rule.setExpiredObjectDeleteMarker(false);
        rule.setNoncurrentVersionExpirationInDays(-1);
//        rule.setNoncurrentVersionExpiration(null);
        rule.setStatus("Enabled");
        BucketLifecycleConfiguration.Transition transition = new BucketLifecycleConfiguration.Transition();
//        transition.setDays(1);

        DateTime parse = DateUtil.parse("2022-12-25T00:00:00.000Z", new FastDateParser(DatePattern.UTC_MS_PATTERN, TimeZone.getTimeZone("GMT"), Locale.CHINA));
        transition.setDate(parse);

        transition.setStorageClass(StorageClass.Glacier);

        rule.setTransitions(ListUtil.of(transition));
        rules.add(rule);
        bucketLifecycleConfiguration.setRules(rules);
        awsS3Client.setBucketLifecycleConfiguration("test-yys", bucketLifecycleConfiguration);

        log.info("end");
    }
}
