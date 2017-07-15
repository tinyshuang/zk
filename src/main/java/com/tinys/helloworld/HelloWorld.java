package com.tinys.helloworld;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class HelloWorld implements Watcher{

    ZooKeeper zk;
    String hostport;

    /**
     * 输出连接的信息
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    public HelloWorld(String hostport) {
        this.hostport = hostport;
    }

    /**
     * 连接zk
     * @throws IOException
     */
    void startZk() throws IOException {
        zk = new ZooKeeper(hostport,15000,this);
    }



    public static  void main(String[] args) throws Exception {
        HelloWorld  hw = new HelloWorld("192.168.198.128:2222");
        hw.startZk();
        Thread.sleep(5000);
    }
}
