package com.example.demotrade.repository;

import com.example.demotrade.entity.ProcessInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程实例仓库接口
 * 用于操作流程实例实体的数据访问层
 */
@Repository
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstanceEntity, String> {
    
    /**
     * 根据业务ID查询流程实例
     * 
     * @param businessId 业务ID
     * @return 流程实例列表
     */
    List<ProcessInstanceEntity> findByBusinessId(String businessId);
    
    /**
     * 根据流程定义ID查询流程实例
     * 
     * @param processDefinitionId 流程定义ID
     * @return 流程实例列表
     */
    List<ProcessInstanceEntity> findByProcessDefinitionId(String processDefinitionId);
    
    /**
     * 根据状态查询流程实例
     * 
     * @param status 流程状态
     * @return 流程实例列表
     */
    List<ProcessInstanceEntity> findByStatus(String status);
    
    /**
     * 根据业务ID和状态查询流程实例
     * 
     * @param businessId 业务ID
     * @param status 流程状态
     * @return 流程实例列表
     */
    List<ProcessInstanceEntity> findByBusinessIdAndStatus(String businessId, String status);
    
    /**
     * 查询指定流程定义ID和业务ID的最新流程实例
     * 
     * @param processDefinitionId 流程定义ID
     * @param businessId 业务ID
     * @return 最新的流程实例
     */
    @Query("SELECT p FROM ProcessInstanceEntity p WHERE p.processDefinitionId = :processDefinitionId AND p.businessId = :businessId ORDER BY p.createTime DESC")
    Optional<ProcessInstanceEntity> findLatestByProcessDefinitionIdAndBusinessId(
            @Param("processDefinitionId") String processDefinitionId,
            @Param("businessId") String businessId);
}