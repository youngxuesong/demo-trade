package com.example.demotrade.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义模型
 * 用于定义一个业务流程的基本信息和结构
 */
public class ProcessDefinition {

    /**
     * 流程定义ID
     */
    private String id;
    
    /**
     * 流程定义名称
     */
    private String name;
    
    /**
     * 流程定义描述
     */
    private String description;
    
    /**
     * 流程定义版本
     */
    private Integer version;
    
    /**
     * 流程节点列表
     */
    private List<ProcessNode> nodes = new ArrayList<>();
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 流程状态：DRAFT-草稿, PUBLISHED-已发布, DEPRECATED-已废弃
     */
    private String status;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<ProcessNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ProcessNode> nodes) {
        this.nodes = nodes;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}