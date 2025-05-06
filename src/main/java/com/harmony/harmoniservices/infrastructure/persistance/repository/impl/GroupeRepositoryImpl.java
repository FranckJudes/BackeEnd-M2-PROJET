
package com.harmony.harmoniservices.infrastructure.persistance.repository.impl;
import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.infrastructure.persistance.mappers.GroupeMapper;
import com.harmony.harmoniservices.infrastructure.persistance.repository.jpa.GroupeRepositoryJpa;
import org.springframework.stereotype.Repository;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.core.ports.repository.GroupeRepository;


@Repository
public class GroupeRepositoryImpl implements GroupeRepository{

    private final GroupeRepositoryJpa groupeRepositoryJpa;


    public GroupeRepositoryImpl(GroupeRepositoryJpa groupeRepositoryjpa){
            this.groupeRepositoryJpa = groupeRepositoryjpa;

    }


    @Override
    public GroupeUtilisateur save(GroupeUtilisateur element) {
       var entity = GroupeMapper.toEntity(element);
       return GroupeMapper.toDomain(groupeRepositoryJpa.save(entity));
    }

    @Override
    public Optional<GroupeUtilisateur> findById(Long id) {
        return groupeRepositoryJpa.findById(id).map(GroupeMapper::toDomain);
    }

    @Override
    public List<GroupeUtilisateur> findAll() {
        return groupeRepositoryJpa.findAll().stream().map(GroupeMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        groupeRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return groupeRepositoryJpa.existsById(id);
    }


    @Override
    public GroupeUtilisateur update(Long id, GroupeUtilisateur element) {
        if (groupeRepositoryJpa.existsById(id)) {
            var entity = GroupeMapper.toEntity(element);
            entity.setId(id);
            return GroupeMapper.toDomain(groupeRepositoryJpa.save(entity));
        } else{
            return null; 
        }
    }
    
}
