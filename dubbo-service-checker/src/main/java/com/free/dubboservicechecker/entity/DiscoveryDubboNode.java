package com.free.dubboservicechecker.entity;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author rarexray
 * @date 2017/11/4 17:25
 * @detail
 */
public class DiscoveryDubboNode {
    /**
     * id
     */
    private Long id;

    /**
     * 类型
     * consumers\provides
     */
    private String type;

    /**
     * 服务ip
     */
    private String host;

    /**
     * 应用名
     */
    private String application;

    /**
     * 服务
     */
    private String service;
    
    //端口
    private String port;
    
    //主机类型
    private String hostType;
    
    //应用类型
    private String appType;
    
    //是否是金融渠道应用
    private String isChl;
    
    //服务类型
    private String srvType;

    
    
    
    /**
     * 发现的原始内容
     */
    private String content;

    private Map<String, String> properties = new Hashtable<>();

    public static DiscoveryDubboNode parseDiscoveryDubboNode(String dubboUrl, String type) {
        DiscoveryDubboNode dubboNode = new DiscoveryDubboNode();
        dubboNode.setType(type);

        try {
            String urlString = URLDecoder.decode(dubboUrl, "UTF-8");

            dubboNode.setContent(urlString);

            urlString = urlString.replaceFirst("consumer://", "http://");
            urlString = urlString.replaceFirst("dubbo://", "http://");
            urlString = urlString.replaceFirst("hessian://", "http://");
            urlString = urlString.replaceFirst("rest://", "http://");
            URL url = new URL(urlString);

            dubboNode.setHost(url.getHost());
            dubboNode.setPort(String.valueOf(url.getPort()));
            String querySpeprator = "&";
            for (String queryPair : url.getQuery().split(querySpeprator)){
                String[] pair = queryPair.split("=");
                if (pair.length == 2) {
                    dubboNode.getProperties().put(pair[0], pair[1]);
                }
                else {
                    dubboNode.getProperties().put(pair[0], "");
                }
            }

            dubboNode.setApplication(dubboNode.getProperties().get("application"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return dubboNode;
    }

    public String getProp(String prop){
        return this.getProperties().get(prop);
    }

    
    @Override
    public String toString() {
        return "DiscoveryDubboNode{" +
                "type='" + type + '\'' +
                ", application='" + application + '\'' +
                ", service='" + service + '\'' +
//                ", content='" + content + '\'' +
                ", host='" + host + '\'' +
                ", properties=" + properties +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHostType() {
		return hostType;
	}

	public void setHostType(String hostType) {
		this.hostType = hostType;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getIsChl() {
		return isChl;
	}

	public void setIsChl(String isChl) {
		this.isChl = isChl;
	}

	public String getSrvType() {
		return srvType;
	}

	public void setSrvType(String srvType) {
		this.srvType = srvType;
	}
    
    
}
