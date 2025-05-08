package com.harmony.harmoniservices.infrastructure.persistance.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.ProcessInstanceEntity;

public interface ProcessInstanceRepositoryJpa extends JpaRepository<ProcessInstanceEntity, Long> {
    
    @Query("SELECT p FROM ProcessInstanceEntity p WHERE p.process.id = :processId AND p.status = 'ACTIVE'")
    List<ProcessInstanceEntity> findActiveInstancesByProcessId(@Param("processId") String processId);
    
    @Query("""
        SELECT DISTINCT p FROM ProcessInstanceEntity p 
        JOIN TaskConfigurationEntity tc ON p.currentTaskId = tc.taskId 
        WHERE p.status = 'ACTIVE' AND tc.assignedUser.id = :userId
    """)
    List<ProcessInstanceEntity> findActiveInstancesWithUserTasks(@Param("userId") Long userId);
    
    @Query("""
        SELECT DISTINCT p FROM ProcessInstanceEntity p 
        JOIN TaskConfigurationEntity tc ON p.currentTaskId = tc.taskId 
        JOIN tc.authorizedGroups g 
        WHERE p.status = 'ACTIVE' AND g.id = :groupId
    """)
    List<ProcessInstanceEntity> findActiveInstancesWithGroupTasks(@Param("groupId") Long groupId);
} 