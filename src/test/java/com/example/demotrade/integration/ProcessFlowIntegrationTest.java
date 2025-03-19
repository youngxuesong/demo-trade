package com.example.demotrade.integration;

import com.example.demotrade.model.ProcessDefinition;
import com.example.demotrade.model.ProcessInstance;
import com.example.demotrade.model.ProcessNode;
import com.example.demotrade.service.ProcessDefinitionService;
import com.example.demotrade.service.ProcessEngineService;
import com.example.demotrade.service.ProcessInstanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 流程引擎集成测试类
 * 用于测试完整的流程执行场景，验证各组件间的协作
 */
@SpringBootTest
public class ProcessFlowIntegrationTest {

    @Autowired
    private ProcessEngineService processEngineService;
    
    @Autowired
    private ProcessDefinitionService processDefinitionService;
    
    @Autowired
    private ProcessInstanceService processInstanceService;
    
    @MockBean
    private RestTemplate restTemplate;
    
    private ProcessDefinition testProcessDefinition;
    private String processDefinitionId;
    
    @BeforeEach
    public void setup() {
        // 创建测试流程定义
        processDefinitionId = "test-process-" + UUID.randomUUID().toString().substring(0, 8);
        testProcessDefinition = createTestProcessDefinition(processDefinitionId);
        
        // 保存流程定义
        processDefinitionService.saveProcessDefinition(testProcessDefinition);
        
        // 模拟外部服务调用结果
        Map<String, Object> approvalResult = new HashMap<>();
        approvalResult.put("approved", true);
        approvalResult.put("comments", "自动审批通过");
        
        Map<String, Object> orderResult = new HashMap<>();
        orderResult.put("orderStatus", "PROCESSED");
        orderResult.put("orderNumber", "ORD-" + System.currentTimeMillis());
        
        // 配置模拟服务调用
        when(restTemplate.postForObject(anyString(), any(), any()))
            .thenReturn(approvalResult)
            .thenReturn(orderResult);
    }
    
    /**
     * 测试完整的流程执行
     * 从流程启动到结束的全过程
     */
    @Test
    public void testCompleteProcessExecution() {
        // 准备测试数据
        String businessId = "order-" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 2500);
        variables.put("customerId", "CUST-001");
        variables.put("productId", "PROD-001");
        
        // 1. 启动流程
        ProcessInstance instance = processEngineService.startProcess(processDefinitionId, businessId, variables);
        
        // 验证流程启动成功
        assertNotNull(instance);
        assertEquals(processDefinitionId, instance.getProcessDefinitionId());
        assertEquals(businessId, instance.getBusinessId());
        assertEquals("RUNNING", instance.getStatus());
        assertEquals("start", instance.getCurrentNodeId());
        
        // 2. 执行第一个节点（开始节点）
        instance = processEngineService.executeNode(instance.getId());
        
        // 验证流程推进到审批节点
        assertEquals("task1", instance.getCurrentNodeId());
        assertEquals("RUNNING", instance.getStatus());
        
        // 3. 执行审批节点
        instance = processEngineService.executeNode(instance.getId());
        
        // 验证流程推进到网关节点
        assertEquals("gateway1", instance.getCurrentNodeId());
        assertTrue(instance.getVariables().containsKey("approved"));
        assertTrue((Boolean) instance.getVariables().get("approved"));
        
        // 4. 执行网关节点
        instance = processEngineService.executeNode(instance.getId());
        
        // 验证流程根据条件推进到正确的节点（通过处理）
        assertEquals("task2", instance.getCurrentNodeId());
        
        // 5. 执行通过处理节点
        instance = processEngineService.executeNode(instance.getId());
        
        // 验证流程推进到结束节点
        assertEquals("end", instance.getCurrentNodeId());
        assertTrue(instance.getVariables().containsKey("orderStatus"));
        assertEquals("PROCESSED", instance.getVariables().get("orderStatus"));
        
        // 6. 完成流程
        instance = processEngineService.completeProcess(instance.getId());
        
        // 验证流程已完成
        assertEquals("COMPLETED", instance.getStatus());
        assertNotNull(instance.getEndTime());
    }
    
    /**
     * 测试流程暂停和恢复
     */
    @Test
    public void testProcessSuspendAndResume() {
        // 准备测试数据
        String businessId = "order-" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 1800);
        
        // 1. 启动流程
        ProcessInstance instance = processEngineService.startProcess(processDefinitionId, businessId, variables);
        
        // 2. 执行到审批节点
        instance = processEngineService.executeNode(instance.getId());
        assertEquals("task1", instance.getCurrentNodeId());
        
        // 3. 暂停流程
        instance = processEngineService.suspendProcess(instance.getId());
        
        // 验证流程已暂停
        assertEquals("SUSPENDED", instance.getStatus());
        
        // 4. 恢复流程
        instance = processEngineService.resumeProcess(instance.getId());
        
        // 验证流程已恢复
        assertEquals("RUNNING", instance.getStatus());
        
        // 5. 继续执行流程
        instance = processEngineService.executeNode(instance.getId());
        assertNotEquals("task1", instance.getCurrentNodeId());
    }
    
    /**
     * 测试流程变量更新
     */
    @Test
    public void testUpdateProcessVariables() {
        // 准备测试数据
        String businessId = "order-" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 1000);
        
        // 1. 启动流程
        ProcessInstance instance = processEngineService.startProcess(processDefinitionId, businessId, variables);
        
        // 2. 更新流程变量
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("amount", 3000);
        updatedVariables.put("priority", "HIGH");
        
        instance = processEngineService.updateProcessVariables(instance.getId(), updatedVariables);
        
        // 验证变量已更新
        assertEquals(3000, instance.getVariables().get("amount"));
        assertEquals("HIGH", instance.getVariables().get("priority"));
    }
    
    /**
     * 创建测试用的流程定义
     */
    private ProcessDefinition createTestProcessDefinition(String processDefinitionId) {
        ProcessDefinition definition = new ProcessDefinition();
        definition.setId(processDefinitionId);
        definition.setName("测试订单处理流程");
        definition.setDescription("用于测试的订单处理流程定义");
        definition.setVersion(1);
        definition.setStatus("PUBLISHED");
        definition.setCreateTime(LocalDateTime.now());
        definition.setUpdateTime(LocalDateTime.now());
        
        // 创建流程节点
        List<ProcessNode> nodes = new ArrayList<>();
        
        // 开始节点
        ProcessNode startNode = new ProcessNode();
        startNode.setId("start");
        startNode.setName("开始");
        startNode.setType("START");
        startNode.getNextNodes().add("task1");
        nodes.add(startNode);
        
        // 审批节点
        ProcessNode task1Node = new ProcessNode();
        task1Node.setId("task1");
        task1Node.setName("订单审批");
        task1Node.setType("TASK");
        task1Node.setServiceName("approval-service");
        task1Node.setOperationName("approve");
        task1Node.setTimeout(10000L);
        task1Node.setRetryCount(3);
        task1Node.setRetryInterval(1000L);
        task1Node.getNextNodes().add("gateway1");
        nodes.add(task1Node);
        
        // 网关节点
        ProcessNode gatewayNode = new ProcessNode();
        gatewayNode.setId("gateway1");
        gatewayNode.setName("审批结果判断");
        gatewayNode.setType("GATEWAY");
        gatewayNode.getNextNodes().add("task2");
        gatewayNode.getNextNodes().add("task3");
        gatewayNode.setCondition("approved == true ? 'task2' : 'task3'");
        nodes.add(gatewayNode);
        
        // 通过处理节点
        ProcessNode task2Node = new ProcessNode();
        task2Node.setId("task2");
        task2Node.setName("订单处理");
        task2Node.setType("TASK");
        task2Node.setServiceName("order-service");
        task2Node.setOperationName("process");
        task2Node.getNextNodes().add("end");
        nodes.add(task2Node);
        
        // 拒绝处理节点
        ProcessNode task3Node = new ProcessNode();
        task3Node.setId("task3");
        task3Node.setName("拒绝通知");
        task3Node.setType("TASK");
        task3Node.setServiceName("notification-service");
        task3Node.setOperationName("notify");
        task3Node.getNextNodes().add("end");
        nodes.add(task3Node);
        
        // 结束节点
        ProcessNode endNode = new ProcessNode();
        endNode.setId("end");
        endNode.setName("结束");
        endNode.setType("END");
        nodes.add(endNode);
        
        definition.setNodes(nodes);
        return definition;
    }
}