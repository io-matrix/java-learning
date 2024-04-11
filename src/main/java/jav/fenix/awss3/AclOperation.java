package jav.fenix.awss3;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AclOperation {

    static volatile int count = 0;


//    static String ENDPOINT = "http://172.38.30.36:7480/";
////    static String AK = "";
////    static String SK = "";
//
//    static String AK = "";
//    static String SK = "";


    static String ENDPOINT = "https://ss-rgw-datalake-jswx.superstor.cn:39443";
    static String AK = "";
    static String SK = "";
    //    Owner: f857f9a69a83b257c1652321186739 (00000180B6047473B60359F57ACA7659)
//    Owner: 19098a70cdead3e931630402350210 (0000017B9B8D8D4DB603D3EAEBDFE323)
    static AmazonS3 awsS3Client = AmazonS3ClientUtil.getAwsS3Client(AK, SK, ENDPOINT);

    public static void main(String[] args) {
        testAcl();
    }


    public static void testHeadBucket() {


        String bucket = "ceshi1";

        HeadBucketResult headBucketResult = awsS3Client.headBucket(new HeadBucketRequest(bucket));
        log.info("{}", JSON.toJSONString(headBucketResult));


//        PutObjectResult error = awsS3Client.putObject(bucket, "test.log", "error");
//        log.info("{}", error);
//
//        List<Bucket> buckets = awsS3Client.listBuckets();
//        log.info("{}", buckets);
//
//
//        ListObjectsV2Result listObjectsV2Result = awsS3Client.listObjectsV2(bucket);
//
//        log.info("{}", JSON.toJSONString(listObjectsV2Result));

    }

    public static void testAcl() {

        String bucket = "test-delete-0";

        AccessControlList bucketAcl = awsS3Client.getBucketAcl(bucket);
        log.info("{}", JSON.toJSONString(bucketAcl));

        AccessControlList accessControlList = new AccessControlList();
        accessControlList.setOwner(new Owner("00000180B6047473B60359F57ACA7659", "f857f9a69a83b257c1652321186739"));
        CanonicalGrantee canonicalGrantee = new CanonicalGrantee("owner");
        canonicalGrantee.setIdentifier("00000180B6047473B60359F57ACA7659");
        canonicalGrantee.setDisplayName("f857f9a69a83b257c1652321186739");
        accessControlList.grantPermission(canonicalGrantee, Permission.parsePermission("FULL_CONTROL"));

        awsS3Client.setBucketAcl(bucket, accessControlList);

        AccessControlList bucketAcl2 = awsS3Client.getBucketAcl(bucket);
        log.info("{}", JSON.toJSONString(bucketAcl2));

    }

}
