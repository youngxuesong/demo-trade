package com.example.demotrade.service.impl;

import com.example.demotrade.entity.ProcessInstanceEntity;
import com.example.demotrade.model.ProcessInstance;
import com.example.demotrade.repository.ProcessInstanceRepository;
import com.example.demotrade.service.ProcessInstanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 流程实例服务实现类
 * 实现流程实例的持久化操作
 */
@Service
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Autowired
    private ProcessInstanceRepository processInstanceRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public ProcessInstance createProcessInstance(ProcessInstance processInstance) {
        // 如果没有ID，生成一个新的UUID
        if (processInstance.getId() == null || processInstance.getId().isEmpty()) {
            processInstance.setId(UUID.randomUUID().toString());
        }
        
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        processInstance.setCreateTime(now);
        processInstance.setUpdateTime(now);
        if (processInstance.getStartTime() == null) {
            processInstance.setStartTime(now);
        }
        
        // 转换为实体对象并保存
        ProcessInstanceEntity entity = convertToEntity(processInstance);
        entity = processInstanceRepository.save(entity);
        
        // 转换回模型对象并返回
        return convertToModel(entity);
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        Optional<ProcessInstanceEntity> entityOpt = processInstanceRepository.findById(processInstanceId);
        return entityOpt.map(this::convertToModel).orElse(null);
    }

    @Override
    public ProcessInstance updateProcessInstance(ProcessInstance processInstance) {
        // 检查实例是否存在
        if (!processInstanceRepository.existsById(processInstance.getId())) {
            throw new RuntimeException("Process instance not found: " + processInstance.getId());
        }
        
        // 更新时间戳
        processInstance.setUpdateTime(LocalDateTime.now());
        
        // 如果状态是COMPLETED或TERMINATED，设置结束时间
        if (("COMPLETED".equals(processInstance.getStatus()) || "TERMINATED".equals(processInstance.getStatus())) 
                && processInstance.getEndTime() == null) {
            processInstance.setEndTime(LocalDateTime.now());
        }
        
        // 转换为实体对象并保存
        ProcessInstanceEntity entity = convertToEntity(processInstance);
        entity = processInstanceRepository.save(entity);
        
        // 转换回模型对象并返回
        return convertToModel(entity);
    }

    @Override
    public List<ProcessInstance> findByBusinessId(String businessId) {
        List<ProcessInstanceEntity> entities = processInstanceRepository.findByBusinessId(businessId);
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInstance> findByStatus(String status) {
        List<ProcessInstanceEntity> entities = processInstanceRepository.findByStatus(status);
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessInstance updateProcessVariables(String processInstanceId, Map<String, Object> variables) {
        // 获取当前实例
        ProcessInstance instance = getProcessInstance(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("Process instance not found: " + processInstanceId);
        }
        
        // 更新变量
        Map<String, Object> currentVars = instance.getVariables();
        if (currentVars == null) {
            currentVars = new HashMap<>();
        }
        currentVars.putAll(variables);
        instance.setVariables(currentVars);
        
        // 更新实例
        return updateProcessInstance(instance);
    }

    @Override
    public ProcessInstance suspendProcessInstance(String processInstanceId) {
        // 获取当前实例
        ProcessInstance instance = getProcessInstance(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("Process instance not found: " + processInstanceId);
        }
        
        // 检查状态
        if (!"RUNNING".equals(instance.getStatus())) {
            throw new RuntimeException("Cannot suspend process instance with status: " + instance.getStatus());
        }
        
        // 更新状态
        instance.setStatus("SUSPENDED");
        
        // 更新实例
        return updateProcessInstance(instance);
    }

    @Override
    public ProcessInstance resumeProcessInstance(String processInstanceId) {
        // 获取当前实例
        ProcessInstance instance = getProcessInstance(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("Process instance not found: " + processInstanceId);
        }
        
        // 检查状态
        if (!"SUSPENDED".equals(instance.getStatus())) {
            throw new RuntimeException("Cannot resume process instance with status: " + instance.getStatus());
        }
        
        // 更新状态
        instance.setStatus("RUNNING");
        
        // 更新实例
        return updateProcessInstance(instance);
    }

    @Override
    public ProcessInstance terminateProcessInstance(String processInstanceId, String errorMessage) {
        // 获取当前实例
        ProcessInstance instance = getProcessInstance(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("Process instance not found: " + processInstanceId);
        }
        
        // 更新状态和错误信息
        instance.setStatus("TERMINATED");
        instance.setErrorMessage(errorMessage);
        instance.setEndTime(LocalDateTime.now());
        
        // 更新实例
        return updateProcessInstance(instance);
    }
    
    /**
     * 将模型对象转换为实体对象
     */
    private ProcessInstanceEntity convertToEntity(ProcessInstance model) {
        ProcessInstanceEntity entity = new ProcessInstanceEntity();
        entity.setId(model.getId());
        entity.setProcessDefinitionId(model.getProcessDefinitionId());
        entity.setProcessDefinitionVersion(model.getProcessDefinitionVersion());
        entity.setBusinessId(model.getBusinessId());
        entity.setCurrentNodeId(model.getCurrentNodeId());
        entity.setStatus(model.getStatus());
        entity.setStartTime(model.getStartTime());
        entity.setEndTime(model.getEndTime());
        entity.setCreateTime(model.getCreateTime());
        entity.setUpdateTime(model.getUpdateTime());
        entity.setErrorMessage(model.getErrorMessage());
        
        // 将变量映射序列化为JSON字符串
        try {
            if (model.getVariables() != null && !model.getVariables().isEmpty()) {
                entity.setVariablesJson(objectMapper.writeValueAsString(model.getVariables()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize process variables", e);
        }
        
        return entity;
    }
    
    /**
     * 将实体对象转换为模型对象
     */
    private ProcessInstance convertToModel(ProcessInstanceEntity entity) {
        ProcessInstance model = new ProcessInstance();
        model.setId(entity.getId());
        model.setProcessDefinitionId(entity.getProcessDefinitionId());
        model.setProcessDefinitionVersion(entity.getProcessDefinitionVersion());
        model.setBusinessId(entity.getBusinessId());
        model.setCurrentNodeId(entity.getCurrentNodeId());
        model.setStatus(entity.getStatus());
        model.setStartTime(entity.getStartTime());
        model.setEndTime(entity.getEndTime());
        model.setCreateTime(entity.getCreateTime());
        model.setUpdateTime(entity.getUpdateTime());
        model.setErrorMessage(entity.getErrorMessage());
        
        // 将JSON字符串反序列化为变量映射
        if (entity.getVariablesJson() != null && !entity.getVariablesJson().isEmpty()) {
            try {
                Map<String, Object> variables = objectMapper.readValue(
                    entity.getVariablesJson(), 
                    new TypeReference<Map<String, Object>>() {}
                );
                model.setVariables(variables);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize process variables", e);
            }
        } else {
            model.setVariables(new HashMap<>());
        }
        
        return model;
    }
}