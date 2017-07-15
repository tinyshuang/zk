package com.tinys.startAsyn;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * Created by asus on 2017/6/18.
 */
public class Master implements Watcher{
    public static final String MASTER = "/master";
    ZooKeeper zk;
    String hostport;



    //主服务器对应的ID
    String serverId = Integer.toString((int) new Random().nextLong());
    //本服务器时候是主服务器的标志
    static  boolean isLeader = false;

    /**
     * 输出连接的信息
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    public Master(String hostport) {
        this.hostport = hostport;
    }

    /**
     * 连接zk
     * @throws IOException
     */
    void startZk() throws IOException {
        zk = new ZooKeeper(hostport,15000,this);
    }

    /**
     * 关闭zk连接
     * @throws InterruptedException
     */
    void stopZk() throws InterruptedException {
        zk.close();
    }


    AsyncCallback.DataCallback masterCheckCallback = new AsyncCallback.DataCallback() {
        public void processResult(int rc  , String path, Object ctx, byte[] bytes, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    //这里相当于递归,再调用一次查询
                    checkMaster();
                    return;
                case NONODE:
                    //当发现没主节点时,主动发起竞争选取主
                    runForMaster();
                    return;
                //其它能获取到值得情况不做操作,表示这时已有其它节点获取到主节点
            }
        }
    };

    /**
     * 检查是否还有必要竞争主选举
     * @return
     */
    void checkMaster(){
        zk.getData(MASTER,false,masterCheckCallback,null);
    }

    AsyncCallback.StringCallback masterCreateCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc  , String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case OK:
                    isLeader = true;
                    break;
                case CONNECTIONLOSS:
                    checkMaster();
                    break;
                default:
                    isLeader = false;

            }

            if (isLeader){
                System.out.println("I am the master");
            }else {
                System.out.println("I am not the master");
            }
        }
    };

    /**
     * 竞争主
     * 注意关闭Linux的防火墙
     */
    public void runForMaster(){
        //异步的create方法无需处理异常
        //因为调用返回前并不会等待create命令的返回,所以无需关心中断异常
        //请求的所有错误信息都会通过回调对象第一个返回,所以也无需关心KeeperException
        zk.create(MASTER,serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,masterCreateCallback,null);

    }


    public static  void main(String[] args) throws Exception {
        Master master = new Master("192.168.198.128:2222");
        master.startZk();
        master.runForMaster();
        Thread.sleep(7000);
        master.stopZk();
    }
}
