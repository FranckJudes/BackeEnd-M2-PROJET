package com.harmony.harmoniservices.core.ports.cases;

import java.util.List;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;

public interface GroupeUtilisateurService {

    GroupeUtilisateur create(GroupeUtilisateur groupeUtilisateur);
    GroupeUtilisateur update(Long id, GroupeUtilisateur groupeUtilisateur);
    List<GroupeUtilisateur> getAll();
    GroupeUtilisateur getById(Long id);
    void delete(Long id);
} 