package com.example.demotrade.service;

import com.example.demotrade.model.ProcessDefinition;

/**
 * 流程定义服务接口
 * 用于管理流程定义的生命周期
 */
public interface ProcessDefinitionService {

    /**
     * 保存流程定义
     * 
     * @param processDefinition 流程定义
     * @return 保存后的流程定义
     */
    ProcessDefinition saveProcessDefinition(ProcessDefinition processDefinition);

    /**
     * 获取指定ID和版本的流程定义
     * 
     * @param processDefinitionId 流程定义ID
     * @param version 版本号
     * @return 流程定义
     */
    ProcessDefinition getProcessDefinition(String processDefinitionId, Integer version);

    /**
     * 获取指定ID的最新版本流程定义
     * 
     * @param processDefinitionId 流程定义ID
     * @return 最新版本的流程定义
     */
    ProcessDefinition getLatestProcessDefinition(String processDefinitionId);

    /**
     * 发布流程定义
     * 
     * @param processDefinitionId 流程定义ID
     * @param version 版本号
     * @return 发布后的流程定义
     */
    ProcessDefinition publishProcessDefinition(String processDefinitionId, Integer version);

    /**
     * 废弃流程定义
     * 
     * @param processDefinitionId 流程定义ID
     * @param version 版本号
     * @return 废弃后的流程定义
     */
    ProcessDefinition deprecateProcessDefinition(String processDefinitionId, Integer version);
}