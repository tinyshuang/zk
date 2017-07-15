package com.tinys.startSyn;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Date;

/**
 * 获取节点的Node信息
 */
public class StatNode  implements Watcher{
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    ZooKeeper zk;
    String hostPort;

    StatNode(String hostport){
        this.hostPort = hostport;
    }

    void startZk() throws IOException {
        zk = new ZooKeeper(hostPort,15000,this);
    }

    void listState() throws KeeperException, InterruptedException {
        try{
            //获取节点信息
            Stat stat = new Stat();
            byte[] datas = zk.getData("/workers",false,stat);
            Date date = new Date(stat.getCtime());
            System.out.println("workers" + new String(datas) + "sinces" + date);
        }catch (KeeperException.NoNodeException e){
            System.out.println("No workers");
        }
    }

    public static  void main(String args[]) throws Exception{
        StatNode statNoed = new StatNode("192.168.198.128:2222");
        statNoed.startZk();
        statNoed.listState();
    }
}
