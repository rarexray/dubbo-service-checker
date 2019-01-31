package com.free.dubboservicechecker.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author rarexray
 * @date 2017/11/4 10:20
 * @detail
 */
public class ZooKeeperClient {
    private ZooKeeper zk;
    /**
     * zookeeper地址
     */

    private String servers = "192.168.110.22:2181";
    /**
     * 链接超时时间
     */
    private int sessionTimeout = 40000;

    private String mainPath = "/dubbo";

    public ZooKeeper getAliveZk() {
        ZooKeeper aliveZk = zk;
        if (aliveZk != null && aliveZk.getState().isAlive()) {
            return aliveZk;
        } else {
            zkReconnect();
            return zk;
        }
    }

    public synchronized void zkReconnect() {
        close();
        try {
            connect();
        } catch (IOException e) {
        }
    }

    public synchronized void close() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
            }
            zk = null;
        }
    }

    private synchronized void connect() throws IOException {
        if (zk == null && !StringUtils.isBlank(servers)) {
            zk = new ZooKeeper(servers, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    // do nothing
                }
            });
        }
    }

    public String getData(String path) {
        String result = null;
        try {
            byte[] data = getAliveZk().getData(path, Boolean.FALSE, null);
            if (null != data) {
                result = new String(data, "UTF-8");
            }
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return result;
    }

    public List<String> getChildren(String path) {
        List<String> data = null;
        try {
            data = getAliveZk().getChildren(path, Boolean.FALSE);
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        }
        return data;
    }

    public List<String> getConsumerChildren(String service){
        return getChildren("/dubbo/"+service+"/consumers");
    }

    public List<String> getProviderChildren(String service){
        return getChildren("/dubbo/"+service+"/providers");
    }

    public List<String> getServiceList(){
        return getChildren(mainPath);
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getMainPath() {
        return mainPath;
    }

    public void setMainPath(String mainPath) {
        this.mainPath = mainPath;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
