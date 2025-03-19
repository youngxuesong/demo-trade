package com.example.demotrade.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProcessDefinition模型的单元测试类
 * 用于验证流程定义模型的基本功能和属性设置
 */
public class ProcessDefinitionTest {

    /**
     * 测试流程定义的基本属性设置和获取
     */
    @Test
    public void testBasicProperties() {
        ProcessDefinition definition = new ProcessDefinition();
        
        // 设置基本属性
        String id = "process-001";
        String name = "测试流程";
        String description = "这是一个测试流程定义";
        Integer version = 1;
        String status = "DRAFT";
        LocalDateTime now = LocalDateTime.now();
        
        definition.setId(id);
        definition.setName(name);
        definition.setDescription(description);
        definition.setVersion(version);
        definition.setStatus(status);
        definition.setCreateTime(now);
        definition.setUpdateTime(now);
        
        // 验证属性值
        assertEquals(id, definition.getId());
        assertEquals(name, definition.getName());
        assertEquals(description, definition.getDescription());
        assertEquals(version, definition.getVersion());
        assertEquals(status, definition.getStatus());
        assertEquals(now, definition.getCreateTime());
        assertEquals(now, definition.getUpdateTime());
    }
    
    /**
     * 测试流程节点列表的操作
     */
    @Test
    public void testNodesOperation() {
        ProcessDefinition definition = new ProcessDefinition();
        
        // 创建测试节点
        ProcessNode startNode = new ProcessNode();
        startNode.setId("start");
        startNode.setType("START");
        
        ProcessNode taskNode = new ProcessNode();
        taskNode.setId("task1");
        taskNode.setType("TASK");
        
        ProcessNode endNode = new ProcessNode();
        endNode.setId("end");
        endNode.setType("END");
        
        // 添加节点到列表
        List<ProcessNode> nodes = new ArrayList<>();
        nodes.add(startNode);
        nodes.add(taskNode);
        nodes.add(endNode);
        
        definition.setNodes(nodes);
        
        // 验证节点列表
        List<ProcessNode> retrievedNodes = definition.getNodes();
        assertNotNull(retrievedNodes);
        assertEquals(3, retrievedNodes.size());
        assertEquals("start", retrievedNodes.get(0).getId());
        assertEquals("task1", retrievedNodes.get(1).getId());
        assertEquals("end", retrievedNodes.get(2).getId());
    }
    
    /**
     * 测试流程定义状态转换
     */
    @Test
    public void testStatusTransition() {
        ProcessDefinition definition = new ProcessDefinition();
        
        // 测试状态转换
        definition.setStatus("DRAFT");
        assertEquals("DRAFT", definition.getStatus());
        
        definition.setStatus("PUBLISHED");
        assertEquals("PUBLISHED", definition.getStatus());
        
        definition.setStatus("DEPRECATED");
        assertEquals("DEPRECATED", definition.getStatus());
    }
}