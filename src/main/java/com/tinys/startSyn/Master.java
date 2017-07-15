package com.tinys.startSyn;

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


    /**
     * 检查是否还有必要竞争主选举
     * @return
     */
    boolean checkMaster(){
        while(true){
            try {
                byte[] datas = zk.getData(MASTER, false, new Stat());
                isLeader = serverId.equals(new String(datas));
                return true;
            } catch (KeeperException.NoNodeException e){
                return false;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 竞争主
     * 注意关闭Linux的防火墙
     */
    public void runForMaster(){
        while (true){
            try {
                zk.create(MASTER, serverId.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
                break;
            }catch (KeeperException.NodeExistsException e){
                //节点存在说明已经存在主节点了
                isLeader = false;
                break;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //检查主节点状态,在无主节点存在的情况下接着竞争主节点
            if (checkMaster()) {
                break;
            }
        }
    }


    public static  void main(String[] args) throws Exception {
        Master master = new Master("192.168.198.128:2222");
        master.startZk();
        master.runForMaster();
        if (isLeader){
            System.out.println("I am the master");
            Thread.sleep(6000);
        }else {
            System.out.println("I am not the master");

        }
        master.stopZk();
    }
}
