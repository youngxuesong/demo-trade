package com.example.demotrade.service;

import com.example.demotrade.model.ProcessInstance;
import java.util.List;
import java.util.Map;

/**
 * 流程实例服务接口
 * 用于管理流程实例的生命周期
 */
public interface ProcessInstanceService {

    /**
     * 创建流程实例
     * 
     * @param processInstance 流程实例
     * @return 创建后的流程实例
     */
    ProcessInstance createProcessInstance(ProcessInstance processInstance);

    /**
     * 获取流程实例
     * 
     * @param processInstanceId 流程实例ID
     * @return 流程实例
     */
    ProcessInstance getProcessInstance(String processInstanceId);

    /**
     * 更新流程实例
     * 
     * @param processInstance 流程实例
     * @return 更新后的流程实例
     */
    ProcessInstance updateProcessInstance(ProcessInstance processInstance);

    /**
     * 根据业务ID查询流程实例
     * 
     * @param businessId 业务ID
     * @return 流程实例列表
     */
    List<ProcessInstance> findByBusinessId(String businessId);

    /**
     * 根据状态查询流程实例
     * 
     * @param status 流程状态
     * @return 流程实例列表
     */
    List<ProcessInstance> findByStatus(String status);

    /**
     * 更新流程实例变量
     * 
     * @param processInstanceId 流程实例ID
     * @param variables 变量映射
     * @return 更新后的流程实例
     */
    ProcessInstance updateProcessVariables(String processInstanceId, Map<String, Object> variables);

    /**
     * 暂停流程实例
     * 
     * @param processInstanceId 流程实例ID
     * @return 暂停后的流程实例
     */
    ProcessInstance suspendProcessInstance(String processInstanceId);

    /**
     * 恢复流程实例
     * 
     * @param processInstanceId 流程实例ID
     * @return 恢复后的流程实例
     */
    ProcessInstance resumeProcessInstance(String processInstanceId);

    /**
     * 终止流程实例
     * 
     * @param processInstanceId 流程实例ID
     * @param errorMessage 错误信息
     * @return 终止后的流程实例
     */
    ProcessInstance terminateProcessInstance(String processInstanceId, String errorMessage);
}