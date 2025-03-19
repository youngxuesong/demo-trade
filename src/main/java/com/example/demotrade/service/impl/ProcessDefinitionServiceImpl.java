package com.example.demotrade.service.impl;

import com.example.demotrade.entity.ProcessDefinitionEntity;
import com.example.demotrade.model.ProcessDefinition;
import com.example.demotrade.model.ProcessNode;
import com.example.demotrade.repository.ProcessDefinitionRepository;
import com.example.demotrade.service.ProcessDefinitionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 流程定义服务实现类
 * 实现流程定义的持久化操作
 */
@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private ProcessDefinitionRepository processDefinitionRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public ProcessDefinition saveProcessDefinition(ProcessDefinition processDefinition) {
        // 检查是否为新流程定义
        boolean isNew = processDefinition.getId() == null || processDefinition.getId().isEmpty();
        
        // 如果是新流程定义，生成ID和设置初始版本
        if (isNew) {
            processDefinition.setId(UUID.randomUUID().toString());
            processDefinition.setVersion(1);
            processDefinition.setStatus("DRAFT");
        } else {
            // 如果是更新现有流程定义，获取最大版本号并加1
            Integer maxVersion = processDefinitionRepository.findMaxVersionByProcessDefinitionId(processDefinition.getId());
            if (maxVersion != null) {
                processDefinition.setVersion(maxVersion + 1);
            } else {
                processDefinition.setVersion(1);
            }
        }
        
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        if (isNew) {
            processDefinition.setCreateTime(now);
        }
        processDefinition.setUpdateTime(now);
        
        // 转换为实体对象并保存
        ProcessDefinitionEntity entity = convertToEntity(processDefinition);
        entity = processDefinitionRepository.save(entity);
        
        // 转换回模型对象并返回
        return convertToModel(entity);
    }

    @Override
    public ProcessDefinition getProcessDefinition(String processDefinitionId, Integer version) {
        Optional<ProcessDefinitionEntity> entityOpt = processDefinitionRepository.findByIdAndVersion(processDefinitionId, version);
        return entityOpt.map(this::convertToModel).orElse(null);
    }

    @Override
    public ProcessDefinition getLatestProcessDefinition(String processDefinitionId) {
        Optional<ProcessDefinitionEntity> entityOpt = processDefinitionRepository.findLatestByProcessDefinitionId(processDefinitionId);
        return entityOpt.map(this::convertToModel).orElse(null);
    }

    @Override
    public ProcessDefinition publishProcessDefinition(String processDefinitionId, Integer version) {
        Optional<ProcessDefinitionEntity> entityOpt = processDefinitionRepository.findByIdAndVersion(processDefinitionId, version);
        if (entityOpt.isPresent()) {
            ProcessDefinitionEntity entity = entityOpt.get();
            entity.setStatus("PUBLISHED");
            entity.setUpdateTime(LocalDateTime.now());
            entity = processDefinitionRepository.save(entity);
            return convertToModel(entity);
        }
        return null;
    }

    @Override
    public ProcessDefinition deprecateProcessDefinition(String processDefinitionId, Integer version) {
        Optional<ProcessDefinitionEntity> entityOpt = processDefinitionRepository.findByIdAndVersion(processDefinitionId, version);
        if (entityOpt.isPresent()) {
            ProcessDefinitionEntity entity = entityOpt.get();
            entity.setStatus("DEPRECATED");
            entity.setUpdateTime(LocalDateTime.now());
            entity = processDefinitionRepository.save(entity);
            return convertToModel(entity);
        }
        return null;
    }
    
    /**
     * 将模型对象转换为实体对象
     */
    private ProcessDefinitionEntity convertToEntity(ProcessDefinition model) {
        ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setVersion(model.getVersion());
        entity.setStatus(model.getStatus());
        entity.setCreateTime(model.getCreateTime());
        entity.setUpdateTime(model.getUpdateTime());
        
        // 将节点列表序列化为JSON字符串
        try {
            entity.setNodesJson(objectMapper.writeValueAsString(model.getNodes()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize process nodes", e);
        }
        
        return entity;
    }
    
    /**
     * 将实体对象转换为模型对象
     */
    private ProcessDefinition convertToModel(ProcessDefinitionEntity entity) {
        ProcessDefinition model = new ProcessDefinition();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setVersion(entity.getVersion());
        model.setStatus(entity.getStatus());
        model.setCreateTime(entity.getCreateTime());
        model.setUpdateTime(entity.getUpdateTime());
        
        // 将JSON字符串反序列化为节点列表
        if (entity.getNodesJson() != null && !entity.getNodesJson().isEmpty()) {
            try {
                List<ProcessNode> nodes = objectMapper.readValue(
                    entity.getNodesJson(), 
                    new TypeReference<List<ProcessNode>>() {}
                );
                model.setNodes(nodes);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize process nodes", e);
            }
        } else {
            model.setNodes(new ArrayList<>());
        }
        
        return model;
    }
}