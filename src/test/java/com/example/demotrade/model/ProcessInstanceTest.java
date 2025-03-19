package com.example.demotrade.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ProcessInstance模型的单元测试类
 * 用于验证流程实例模型的基本功能和属性设置
 */
public class ProcessInstanceTest {

    /**
     * 测试流程实例的基本属性设置和获取
     */
    @Test
    public void testBasicProperties() {
        ProcessInstance instance = new ProcessInstance();
        
        // 设置基本属性
        String id = "instance-001";
        String processDefinitionId = "process-001";
        Integer processDefinitionVersion = 1;
        String businessId = "order-12345";
        String currentNodeId = "task1";
        String status = "RUNNING";
        LocalDateTime now = LocalDateTime.now();
        String errorMessage = "测试错误信息";
        
        instance.setId(id);
        instance.setProcessDefinitionId(processDefinitionId);
        instance.setProcessDefinitionVersion(processDefinitionVersion);
        instance.setBusinessId(businessId);
        instance.setCurrentNodeId(currentNodeId);
        instance.setStatus(status);
        instance.setStartTime(now);
        instance.setEndTime(now);
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setErrorMessage(errorMessage);
        
        // 验证属性值
        assertEquals(id, instance.getId());
        assertEquals(processDefinitionId, instance.getProcessDefinitionId());
        assertEquals(processDefinitionVersion, instance.getProcessDefinitionVersion());
        assertEquals(businessId, instance.getBusinessId());
        assertEquals(currentNodeId, instance.getCurrentNodeId());
        assertEquals(status, instance.getStatus());
        assertEquals(now, instance.getStartTime());
        assertEquals(now, instance.getEndTime());
        assertEquals(now, instance.getCreateTime());
        assertEquals(now, instance.getUpdateTime());
        assertEquals(errorMessage, instance.getErrorMessage());
    }
    
    /**
     * 测试流程变量的操作
     */
    @Test
    public void testVariablesOperation() {
        ProcessInstance instance = new ProcessInstance();
        
        // 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", "ORD-001");
        variables.put("amount", 1500.50);
        variables.put("approved", true);
        variables.put("items", new String[]{"item1", "item2"});
        
        instance.setVariables(variables);
        
        // 验证流程变量
        Map<String, Object> retrievedVars = instance.getVariables();
        assertNotNull(retrievedVars);
        assertEquals(4, retrievedVars.size());
        assertEquals("ORD-001", retrievedVars.get("orderId"));
        assertEquals(1500.50, retrievedVars.get("amount"));
        assertEquals(true, retrievedVars.get("approved"));
        assertArrayEquals(new String[]{"item1", "item2"}, (String[])retrievedVars.get("items"));
    }
    
    /**
     * 测试流程状态转换
     */
    @Test
    public void testStatusTransition() {
        ProcessInstance instance = new ProcessInstance();
        
        // 测试状态转换
        instance.setStatus("RUNNING");
        assertEquals("RUNNING", instance.getStatus());
        
        instance.setStatus("SUSPENDED");
        assertEquals("SUSPENDED", instance.getStatus());
        
        instance.setStatus("COMPLETED");
        assertEquals("COMPLETED", instance.getStatus());
        
        instance.setStatus("TERMINATED");
        assertEquals("TERMINATED", instance.getStatus());
        
        instance.setStatus("FAILED");
        assertEquals("FAILED", instance.getStatus());
    }
    
    /**
     * 测试错误信息处理
     */
    @Test
    public void testErrorHandling() {
        ProcessInstance instance = new ProcessInstance();
        
        // 初始状态下错误信息为空
        assertNull(instance.getErrorMessage());
        
        // 设置错误信息
        String errorMessage = "服务调用超时: testService";
        instance.setErrorMessage(errorMessage);
        instance.setStatus("FAILED");
        
        // 验证错误信息和状态
        assertEquals(errorMessage, instance.getErrorMessage());
        assertEquals("FAILED", instance.getStatus());
    }
}