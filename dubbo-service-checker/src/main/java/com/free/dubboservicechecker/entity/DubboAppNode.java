package com.free.dubboservicechecker.entity;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author rarexray
 * @date 2019/1/29 下午5:08
 */

@Data
public class DubboAppNode {
    private static final Logger logger = LoggerFactory.getLogger(DubboAppNode.class);

    private String name;

    private List<String> servicesList = new ArrayList<>();

    private Set<DubboAppNode> provider = new HashSet<>();

    private Set<DubboAppNode> consumer = new HashSet<>();

    @Override
    public String toString() {
        return "DubboAppNode{" +
                "name='" + name + '\'' +
                ", servicesList=" + servicesList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DubboAppNode that = (DubboAppNode) o;

        if (!name.equals(that.name)) return false;
        return servicesList.equals(that.servicesList);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + servicesList.hashCode();
        return result;
    }
}
