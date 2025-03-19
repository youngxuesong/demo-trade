package com.example.demotrade.service;

import com.example.demotrade.model.ProcessDefinition;
import com.example.demotrade.model.ProcessInstance;
import com.example.demotrade.model.ProcessNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 流程引擎服务的单元测试类
 * 用于测试流程执行的核心逻辑
 */
public class ProcessEngineServiceTest {

    @Mock
    private ProcessDefinitionService processDefinitionService;
    
    @Mock
    private ProcessInstanceService processInstanceService;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private ProcessEngineService processEngineService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试启动流程实例
     */
    @Test
    public void testStartProcess() {
        // 准备测试数据
        String processDefinitionId = "process-001";
        String businessId = "order-12345";
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 2000);
        variables.put("customerId", "C001");
        
        // 模拟流程定义
        ProcessDefinition definition = createTestProcessDefinition(processDefinitionId);
        when(processDefinitionService.getLatestProcessDefinition(processDefinitionId)).thenReturn(definition);
        
        // 模拟流程实例创建
        ProcessInstance instance = new ProcessInstance();
        instance.setId("instance-001");
        instance.setProcessDefinitionId(processDefinitionId);
        instance.setProcessDefinitionVersion(1);
        instance.setBusinessId(businessId);
        instance.setStatus("RUNNING");
        instance.setCurrentNodeId("start");
        instance.setVariables(variables);
        
        when(processInstanceService.createProcessInstance(any(ProcessInstance.class))).thenReturn(instance);
        
        // 执行测试
        ProcessInstance result = processEngineService.startProcess(processDefinitionId, businessId, variables);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("instance-001", result.getId());
        assertEquals(processDefinitionId, result.getProcessDefinitionId());
        assertEquals(businessId, result.getBusinessId());
        assertEquals("RUNNING", result.getStatus());
        assertEquals(2, result.getVariables().size());
    }
    
    /**
     * 测试执行流程节点
     */
    @Test
    public void testExecuteNode() {
        // 准备测试数据
        String processInstanceId = "instance-001";
        String nodeId = "task1";
        
        // 模拟流程实例
        ProcessInstance instance = new ProcessInstance();
        instance.setId(processInstanceId);
        instance.setProcessDefinitionId("process-001");
        instance.setProcessDefinitionVersion(1);
        instance.setBusinessId("order-12345");
        instance.setStatus("RUNNING");
        instance.setCurrentNodeId(nodeId);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 2000);
        instance.setVariables(variables);
        
        when(processInstanceService.getProcessInstance(processInstanceId)).thenReturn(instance);
        
        // 模拟流程定义
        ProcessDefinition definition = createTestProcessDefinition("process-001");
        when(processDefinitionService.getProcessDefinition("process-001", 1)).thenReturn(definition);
        
        // 模拟服务调用结果
        Map<String, Object> serviceResult = new HashMap<>();
        serviceResult.put("approved", true);
        serviceResult.put("comments", "自动审批通过");
        
        when(restTemplate.postForObject(anyString(), any(), any())).thenReturn(serviceResult);
        
        // 模拟更新流程实例
        ProcessInstance updatedInstance = new ProcessInstance();
        updatedInstance.setId(processInstanceId);
        updatedInstance.setCurrentNodeId("task2");
        updatedInstance.setStatus("RUNNING");
        
        Map<String, Object> updatedVariables = new HashMap<>(variables);
        updatedVariables.putAll(serviceResult);
        updatedInstance.setVariables(updatedVariables);
        
        when(processInstanceService.updateProcessInstance(any(ProcessInstance.class))).thenReturn(updatedInstance);
        
        // 执行测试
        ProcessInstance result = processEngineService.executeNode(processInstanceId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(processInstanceId, result.getId());
        assertEquals("task2", result.getCurrentNodeId());
        assertEquals("RUNNING", result.getStatus());
        assertTrue(result.getVariables().containsKey("approved"));
        assertTrue(result.getVariables().containsKey("comments"));
    }
    
    /**
     * 测试完成流程
     */
    @Test
    public void testCompleteProcess() {
        // 准备测试数据
        String processInstanceId = "instance-001";
        
        // 模拟流程实例
        ProcessInstance instance = new ProcessInstance();
        instance.setId(processInstanceId);
        instance.setProcessDefinitionId("process-001");
        instance.setProcessDefinitionVersion(1);
        instance.setBusinessId("order-12345");
        instance.setStatus("RUNNING");
        instance.setCurrentNodeId("end");
        
        when(processInstanceService.getProcessInstance(processInstanceId)).thenReturn(instance);
        
        // 模拟流程定义
        ProcessDefinition definition = createTestProcessDefinition("process-001");
        when(processDefinitionService.getProcessDefinition("process-001", 1)).thenReturn(definition);
        
        // 模拟更新流程实例
        ProcessInstance completedInstance = new ProcessInstance();
        completedInstance.setId(processInstanceId);
        completedInstance.setStatus("COMPLETED");
        
        when(processInstanceService.updateProcessInstance(any(ProcessInstance.class))).thenReturn(completedInstance);
        
        // 执行测试
        ProcessInstance result = processEngineService.completeProcess(processInstanceId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(processInstanceId, result.getId());
        assertEquals("COMPLETED", result.getStatus());
    }
    
    /**
     * 创建测试用的流程定义
     */
    private ProcessDefinition createTestProcessDefinition(String processDefinitionId) {
        ProcessDefinition definition = new ProcessDefinition();
        definition.setId(processDefinitionId);
        definition.setName("测试流程");
        definition.setVersion(1);
        definition.setStatus("PUBLISHED");
        
        // 创建流程节点
        List<ProcessNode> nodes = new ArrayList<>();
        
        // 开始节点
        ProcessNode startNode = new ProcessNode();
        startNode.setId("start");
        startNode.setName("开始");
        startNode.setType("START");
        startNode.getNextNodes().add("task1");
        nodes.add(startNode);
        
        // 任务节点1
        ProcessNode task1Node = new ProcessNode();
        task1Node.setId("task1");
        task1Node.setName("审批任务");
        task1Node.setType("TASK");
        task1Node.setServiceName("approval-service");
        task1Node.setOperationName("approve");
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
        
        // 任务节点2
        ProcessNode task2Node = new ProcessNode();
        task2Node.setId("task2");
        task2Node.setName("通过处理");
        task2Node.setType("TASK");
        task2Node.setServiceName("order-service");
        task2Node.setOperationName("process");
        task2Node.getNextNodes().add("end");
        nodes.add(task2Node);
        
        // 任务节点3
        ProcessNode task3Node = new ProcessNode();
        task3Node.setId("task3");
        task3Node.setName("拒绝处理");
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