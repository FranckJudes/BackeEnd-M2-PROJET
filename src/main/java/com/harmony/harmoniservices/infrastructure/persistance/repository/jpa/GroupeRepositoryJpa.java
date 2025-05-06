package com.harmony.harmoniservices.infrastructure.persistance.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harmony.harmoniservices.infrastructure.persistance.entitites.GroupeEntity;

public interface GroupeRepositoryJpa extends JpaRepository<GroupeEntity, Long> {
}