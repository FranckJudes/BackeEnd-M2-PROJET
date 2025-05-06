package com.harmony.harmoniservices.core.ports.repository;

import java.util.List;
import java.util.Optional;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;

public interface GroupeRepository {

        GroupeUtilisateur save(GroupeUtilisateur element);
        GroupeUtilisateur update(Long id,GroupeUtilisateur element);
        Optional<GroupeUtilisateur> findById(Long id);
        List<GroupeUtilisateur> findAll();
        void deleteById(Long id);
        boolean existsById(Long id);
    
} 