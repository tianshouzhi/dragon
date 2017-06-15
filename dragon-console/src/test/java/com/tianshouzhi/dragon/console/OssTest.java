package com.tianshouzhi.dragon.console;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

import java.io.*;

/**
 * Created by tianshouzhi on 2017/6/15.
 */
public class OssTest {
    private static String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private static String accessKeyId = "1L7MkKmXiULe0eJi";
    private static String accessKeySecret = "o0xnRFjpkqz5noGKIUJtIaHs3ues1P";
    private static String bucketName = "tianshouzhi";
    public static void main(String[] args) throws IOException {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String path = "oss-java-sdk.txt";
        PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(bucketName, path, createSampleFile()));
        ResponseMessage response = putObjectResult.getResponse();
        System.out.println(putObjectResult);
    }

    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("oss-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("0123456789011234567890\n");
        writer.close();

        return file;
    }
}
