package com.free.dubboservicechecker;

import com.free.dubboservicechecker.entity.DiscoveryDubboNode;
import com.free.dubboservicechecker.entity.DubboAppNode;
import com.free.dubboservicechecker.entity.ZooKeeperClient;
import com.free.dubboservicechecker.util.CycleDetector;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author rarexray
 * @date 2019/1/9 下午3:23
 */
public class ServiceChecker {
    private static final Logger logger = LoggerFactory.getLogger(ServiceChecker.class);


    private static final String regserver = "192.168.110.22:2181";
    private static final int timeOut = 6000;
    public static final String TYPE_PROVIDER = "provider";
    public static final String TYPE_CONSUMER = "consumer";

    private ZooKeeperClient zkClient = null;

    HashMap<String, DubboAppNode> appMap = new HashMap<>();
    HashMap<String, String> serviceNotAvailableMap = new HashMap<>();

    @Synchronized
    private ZooKeeperClient getZkClient(){
        if (zkClient == null){
            zkClient = new ZooKeeperClient();
            zkClient.setServers(regserver);
            zkClient.setSessionTimeout(timeOut);

        }

        return zkClient;
    }

    /**
     * parse all the nodes info on zk
     */
    public void buildEnv(){
        List<String> serviceList = getZkClient().getServiceList();


        for (String service : serviceList){

            List<String> nodeList = getZkClient().getProviderChildren(service);

            for (String nodeurl : nodeList){
                DiscoveryDubboNode dubboNode = DiscoveryDubboNode.parseDiscoveryDubboNode(nodeurl, TYPE_PROVIDER);

                DubboAppNode app = appMap.get(dubboNode.getApplication());
                if (app == null){
                    app = new DubboAppNode();
                    app.setName(dubboNode.getApplication());
                    app.getServicesList().add(service);

                    appMap.put(app.getName(), app);
                }
            }

        }


        for (String service : serviceList){

            List<String> nodeList = getZkClient().getConsumerChildren(service);

            for (String nodeurl : nodeList){
                DiscoveryDubboNode dubboNode = DiscoveryDubboNode.parseDiscoveryDubboNode(nodeurl, TYPE_CONSUMER);

                DubboAppNode consumerApp = appMap.get(dubboNode.getApplication());
                if (consumerApp == null){
                    serviceNotAvailableMap.put(service, dubboNode.getApplication());
                    continue;
                }

                List<String> providers = getZkClient().getProviderChildren(service);

                for (String providersUrl : providers){
                    DiscoveryDubboNode providerNode = DiscoveryDubboNode.parseDiscoveryDubboNode(providersUrl, TYPE_PROVIDER);

                    DubboAppNode providerApp = appMap.get(providerNode.getApplication());
                    if (providerApp == null){
                        logger.error("some problem??！！！！ {}", service);
                        continue;
                    }

                    if (providerApp == consumerApp){
                        continue;
                    }
                    consumerApp.getProvider().add(providerApp);
                    providerApp.getConsumer().add(consumerApp);

                }

            }

        }



        return ;
    }
    /**
     * check app is running by checking the app is register on zk
     * @param ipHost
     * @param appName
     * @param type
     * @param service
     * @return
     */
    public boolean isDubboAppRunning(String ipHost, String appName, String type, String service) {



        List<String> nodeList = null;
        if (type.equals(TYPE_CONSUMER)){
            nodeList = getZkClient().getConsumerChildren(service);
        }
        else if (type.equals(TYPE_PROVIDER)){
            nodeList = getZkClient().getProviderChildren(service);
        }

        if (nodeList != null){
            for (String nodeurl : nodeList){
                DiscoveryDubboNode dubboNode = DiscoveryDubboNode.parseDiscoveryDubboNode(nodeurl, type);

                if(dubboNode.getApplication()==null||"".equals(dubboNode.getApplication())){
                    continue;
                }
                if (appName.equals(dubboNode.getApplication())){
                    if (ipHost.equals(dubboNode.getHost())){
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * 打印注册了但没有provider的服务
     * print regist consumer but no provider available
     */
    public void printNoProviderService(){

        for (String service : serviceNotAvailableMap.keySet()){
            logger.info("service {}, need app {} not avaible", service, serviceNotAvailableMap.get(service));
        }
    }

    /**
     * 打印被最多依赖的服务
     */
    public void printMostDepandencedService(){

        List<DubboAppNode> result = new ArrayList<>(appMap.values());
        Collections.sort(result, (app1, app2) -> app2.getConsumer().size() - app1.getConsumer().size());

        for (int i = 0; i < 20; i++){
            logger.info("{} - {}", result.get(i).getName(), result.get(i).getConsumer().size());
        }
    }

    /**
     * 打印有循环引用的应用
     * print the import cycle
     */
    public void printCycleService(){

        List<DubboAppNode> result = new ArrayList<>(appMap.values());
        CycleDetector cycleDetector =  new CycleDetector();

        boolean hasCycle = cycleDetector.containsCycle(result);

        if (hasCycle) {
            List cycleList = cycleDetector.getLocalCycleList();

            logger.info("local cycle {}", cycleList);
        }
    }

}
