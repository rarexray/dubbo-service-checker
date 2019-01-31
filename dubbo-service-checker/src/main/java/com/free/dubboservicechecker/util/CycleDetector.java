package com.free.dubboservicechecker.util;

import com.free.dubboservicechecker.entity.DubboAppNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 抄了一个环检测
 * @author rarexray
 * @date 2019/1/30 上午9:35
 */
public class CycleDetector
{
    private static final Logger logger = LoggerFactory.getLogger(CycleDetector.class);

    private static final String marked = "marked";

    private static final String complete = "complete";

    private Map<DubboAppNode, String> marks;

    private List<String> verticesInCycles;


    private List<Set<String>> localCycleList;

    public CycleDetector()
    {
        marks = new HashMap<>();
        verticesInCycles = new ArrayList<>();
        localCycleList = new ArrayList<>();
    }

    public boolean containsCycle(List<DubboAppNode> list)
    {
        for (DubboAppNode v : list)
        {
            // 如果v正在遍历或者遍历完成,不需要进入mark(),因为mark是一个递归调用，使用的是深度优先搜索算法;
            // 这是为了保证1个顶点只会遍历一次
            if (!marks.containsKey(v))
            {
                if (mark(v))
                {
                    // return true;
                }
            }
        }

        return !verticesInCycles.isEmpty();
    }

    //DFS算法,遍历顶点vertex
    // @return 当前顶点是否在环上
    private boolean mark(DubboAppNode vertex)
    {
        Set<String> localCycles = new HashSet<>();

        // 当前顶点vertex,遍历开始
        marks.put(vertex, marked);

        for (DubboAppNode u : vertex.getProvider())
        {
            // u的遍历还没有结束,说明存在u->vertex的通路,也存在vertex->u的通路,形成了循环
            if (marks.containsKey(u) && marks.get(u).equals(marked))
            {
                localCycles.add(vertex.getName());
                localCycles.add(u.getName());
                // return true;
            }
            else if (!marks.containsKey(u))
            {
                if (mark(u))
                {
                    localCycles.add(vertex.getName());
                    localCycles.add(u.getName());
                    // return true;
                }
            }
        }

        // 当前顶点vertex,遍历完成
        marks.put(vertex, complete);

        if (localCycles.size() > 0) {
            localCycleList.add(localCycles);
        }

        verticesInCycles.addAll(localCycles);
        return !localCycles.isEmpty();
    }

    public List<String> getVerticesInCycles()
    {
        return verticesInCycles;
    }

    public List<Set<String>> getLocalCycleList() {
        return localCycleList;
    }

    public void setLocalCycleList(List<Set<String>> localCycleList) {
        this.localCycleList = localCycleList;
    }

}
