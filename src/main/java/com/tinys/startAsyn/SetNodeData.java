package com.tinys.startAsyn;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * 设计到设置节点值得知识
 * 以及注意重试时状态会乱序的处理
 */
public class SetNodeData implements Watcher{
    ZooKeeper zk;
    String hostport;
    String status;

    //主服务器对应的ID
    String serverId = Integer.toString((int) new Random().nextLong());

    /**
     * 输出连接的信息
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        System.out.println(watchedEvent);
    }

    public SetNodeData(String hostport) {
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


    AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    register();
                    break;
                case OK:
                    System.out.println("Register successfully:" + serverId);
                    break;
                case NODEEXISTS:
                    System.out.println("Already regiater : " + serverId);
                    break;
                default:
                    System.out.println("Something wrong" + path + KeeperException.create(KeeperException.Code.get(rc)));
            }
        }
    };

    /**
     * 创建一个从节点,并将节点的值设置为"IDLE"空闲状态
     */
    private void register(){
        zk.create("/workers/worker-"+serverId,
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                createWorkerCallback,
                null);
    }



    //以下为改变节点状态的方法,也就是设置节点值得方法
    AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (KeeperException.Code.get(rc)){
                case CONNECTIONLOSS:
                    //连接丢失,再次调用更新事件
                    updateStatus((String) ctx);
                    return;
            }
        }
    };

    /**
     * 更新状态的方法 : 在初始更新和重试更新都会调用本方法
     * @param status
     */
    synchronized private void updateStatus(String status){
        //判断状态相同才更新,否则丢弃乱序的更新,只需要更新最终状态的status
        if (status == this.status){
            zk.setData("/workers/"+serverId,
                    status.getBytes(),
                    -1,//-1表示禁止版本号检查
                    statusUpdateCallback,
                    status);
        }
    }

    public void setStatus(String status){
        //先保存到本地状态,万一更新失败可重试
        this.status = status;
        updateStatus(status);
    }

    public static  void main(String[] args) throws Exception {
        SetNodeData snd = new SetNodeData("192.168.198.128:2222");
        snd.startZk();
        snd.register();
        snd.stopZk();
    }
}
