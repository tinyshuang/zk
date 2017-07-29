package com.tinys.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by asus on 2017/7/9.
 */
public class HelloWorld {
    static  String hostport = "192.168.198.128:2222";

    private static CuratorFramework zkc = CuratorFrameworkFactory.newClient(hostport,new ExponentialBackoffRetry(1000, 3));

    private static void start(){
        zkc.start();
    }

    private static void stop(){
        zkc.close();
    }



    /**
     * 流畅式API
     * @throws Exception
     */
    private static void create() throws Exception {
        zkc.create().withMode(CreateMode.PERSISTENT).forPath("/testCurator","test".getBytes());
    }

    public static  void main(String[] args) throws Exception {
        start();
        create();
        stop();
    }

}
