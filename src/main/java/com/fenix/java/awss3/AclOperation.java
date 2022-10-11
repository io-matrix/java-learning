package com.fenix.java.awss3;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AclOperation {

    static volatile int count = 0;


//    static String ENDPOINT = "http://172.38.30.36:7480/";
////    static String AK = "1D9Q6BXNZR0806NW0O96";
////    static String SK = "reMgC6zUtnexFYwraApvTguyewSnIRvU5792geSM";
//
//    static String AK = "3U3N3YVE93EY8G0PJ7SJ";
//    static String SK = "lkEf7KMHlGvOLHlx8LVmiYUHdJVynpQmuuzSCQWl";


    static String ENDPOINT = "https://haerbin-woyun.datalake.cn:39443";
    static String AK = "7LQE8SSHRW6PL53XP16F";
    static String SK = "rYjkU2GqgVi3dh6VStnkoQSiS3ofqtM8SsA7rnMZ";

    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        testAcl();
    }


    public static void testHeadBucket() {

        String bucket = "test-0926";

        PutObjectResult error = awsS3Client.putObject(bucket, "test.log", "error");
        log.info("{}", error);

        List<Bucket> buckets = awsS3Client.listBuckets();
        log.info("{}", buckets);


        ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(bucket);

        log.info("{}", JSON.toJSONString(listObjectsV2Result));

        HeadBucketResult headBucketResult = awsS3Client.headBucket(new HeadBucketRequest(bucket));
        log.info("{}", JSON.toJSONString(headBucketResult));

    }

    public static void testAcl() {

        String bucket = "acl-test";

        AccessControlList bucketAcl = awsS3Client.getBucketAcl(bucket);
        log.info("{}", JSON.toJSONString(bucketAcl));

        AccessControlList accessControlList = new AccessControlList();
        accessControlList.setOwner(new Owner("f0d81a-1626333178213", "f0d81a-1626333178213"));
        CanonicalGrantee canonicalGrantee = new CanonicalGrantee("ehualu-back");
        canonicalGrantee.setIdentifier("ehualu-back");
        canonicalGrantee.setDisplayName("ehualu-back");
        accessControlList.grantPermission(canonicalGrantee, Permission.parsePermission("FULL_CONTROL"));

        awsS3Client.setBucketAcl(bucket, accessControlList);

        AccessControlList bucketAcl2 = awsS3Client.getBucketAcl(bucket);
        log.info("{}", JSON.toJSONString(bucketAcl2));

    }

}
