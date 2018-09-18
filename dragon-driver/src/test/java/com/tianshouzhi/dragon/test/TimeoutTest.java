package com.tianshouzhi.dragon.test;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by tianshouzhi on 2018/6/1.
 */
public class TimeoutTest {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        try{
            Socket client=new Socket("127.0.0.1",3306);
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            System.out.println((System.currentTimeMillis() - start) / 1000);
        }
    }
}
