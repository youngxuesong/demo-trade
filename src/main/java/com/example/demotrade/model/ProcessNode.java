package com.example.demotrade.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程节点模型
 * 用于定义流程中的一个节点及其属性
 */
public class ProcessNode {

    /**
     * 节点ID
     */
    private String id;
    
    /**
     * 节点名称
     */
    private String name;
    
    /**
     * 节点类型：START-开始节点, TASK-任务节点, GATEWAY-网关节点, END-结束节点
     */
    private String type;
    
    /**
     * 节点描述
     */
    private String description;
    
    /**
     * 节点执行的服务名称（对于任务节点）
     */
    private String serviceName;
    
    /**
     * 节点执行的操作名称（对于任务节点）
     */
    private String operationName;
    
    /**
     * 节点参数配置
     */
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 后续节点列表
     */
    private List<String> nextNodes = new ArrayList<>();
    
    /**
     * 条件表达式（对于网关节点）
     */
    private String condition;
    
    /**
     * 超时时间（毫秒）
     */
    private Long timeout;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 重试间隔（毫秒）
     */
    private Long retryInterval;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public List<String> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(List<String> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Long retryInterval) {
        this.retryInterval = retryInterval;
    }
}