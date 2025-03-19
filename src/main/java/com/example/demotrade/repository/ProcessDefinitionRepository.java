package com.example.demotrade.repository;

import com.example.demotrade.entity.ProcessDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 流程定义仓库接口
 * 用于操作流程定义实体的数据访问层
 */
@Repository
public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinitionEntity, String> {
    
    /**
     * 根据流程定义ID和版本号查询流程定义
     * 
     * @param processDefinitionId 流程定义ID
     * @param version 版本号
     * @return 流程定义实体
     */
    Optional<ProcessDefinitionEntity> findByIdAndVersion(String processDefinitionId, Integer version);
    
    /**
     * 查询指定流程定义ID的最新版本
     * 
     * @param processDefinitionId 流程定义ID
     * @return 最新版本的流程定义实体
     */
    @Query("SELECT p FROM ProcessDefinitionEntity p WHERE p.id = :processDefinitionId AND p.status = 'PUBLISHED' ORDER BY p.version DESC")
    Optional<ProcessDefinitionEntity> findLatestByProcessDefinitionId(@Param("processDefinitionId") String processDefinitionId);
    
    /**
     * 查询指定流程定义ID的最大版本号
     * 
     * @param processDefinitionId 流程定义ID
     * @return 最大版本号
     */
    @Query("SELECT MAX(p.version) FROM ProcessDefinitionEntity p WHERE p.id = :processDefinitionId")
    Integer findMaxVersionByProcessDefinitionId(@Param("processDefinitionId") String processDefinitionId);
}