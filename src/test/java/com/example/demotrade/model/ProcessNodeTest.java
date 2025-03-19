package com.example.demotrade.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProcessNode模型的单元测试类
 * 用于验证流程节点模型的基本功能和属性设置
 */
public class ProcessNodeTest {

    /**
     * 测试节点基本属性的设置和获取
     */
    @Test
    public void testBasicProperties() {
        ProcessNode node = new ProcessNode();
        
        // 设置基本属性
        String id = "node-001";
        String name = "测试节点";
        String type = "TASK";
        String description = "这是一个测试节点";
        String serviceName = "testService";
        String operationName = "testOperation";
        Long timeout = 5000L;
        Integer retryCount = 3;
        Long retryInterval = 1000L;
        
        node.setId(id);
        node.setName(name);
        node.setType(type);
        node.setDescription(description);
        node.setServiceName(serviceName);
        node.setOperationName(operationName);
        node.setTimeout(timeout);
        node.setRetryCount(retryCount);
        node.setRetryInterval(retryInterval);
        
        // 验证属性值
        assertEquals(id, node.getId());
        assertEquals(name, node.getName());
        assertEquals(type, node.getType());
        assertEquals(description, node.getDescription());
        assertEquals(serviceName, node.getServiceName());
        assertEquals(operationName, node.getOperationName());
        assertEquals(timeout, node.getTimeout());
        assertEquals(retryCount, node.getRetryCount());
        assertEquals(retryInterval, node.getRetryInterval());
    }
    
    /**
     * 测试节点参数配置的操作
     */
    @Test
    public void testParametersOperation() {
        ProcessNode node = new ProcessNode();
        
        // 设置参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", 100);
        parameters.put("param3", true);
        
        node.setParameters(parameters);
        
        // 验证参数
        Map<String, Object> retrievedParams = node.getParameters();
        assertNotNull(retrievedParams);
        assertEquals(3, retrievedParams.size());
        assertEquals("value1", retrievedParams.get("param1"));
        assertEquals(100, retrievedParams.get("param2"));
        assertEquals(true, retrievedParams.get("param3"));
    }
    
    /**
     * 测试后续节点列表的操作
     */
    @Test
    public void testNextNodesOperation() {
        ProcessNode node = new ProcessNode();
        
        // 设置后续节点
        List<String> nextNodes = new ArrayList<>();
        nextNodes.add("node1");
        nextNodes.add("node2");
        nextNodes.add("node3");
        
        node.setNextNodes(nextNodes);
        
        // 验证后续节点列表
        List<String> retrievedNodes = node.getNextNodes();
        assertNotNull(retrievedNodes);
        assertEquals(3, retrievedNodes.size());
        assertEquals("node1", retrievedNodes.get(0));
        assertEquals("node2", retrievedNodes.get(1));
        assertEquals("node3", retrievedNodes.get(2));
    }
    
    /**
     * 测试网关节点条件表达式
     */
    @Test
    public void testGatewayCondition() {
        ProcessNode node = new ProcessNode();
        
        // 设置网关条件
        node.setType("GATEWAY");
        String condition = "amount > 1000";
        node.setCondition(condition);
        
        // 验证条件表达式
        assertEquals(condition, node.getCondition());
    }
}