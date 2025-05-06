package com.harmony.harmoniservices.core.cases;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.core.ports.cases.GroupeUtilisateurService;
import com.harmony.harmoniservices.core.ports.repository.GroupeRepository;
import org.springframework.dao.DataIntegrityViolationException;


@Service
public class GroupeUtilisateurImpl implements GroupeUtilisateurService{

    public final GroupeRepository groupeRepository;

    GroupeUtilisateurImpl(GroupeRepository groupeRepository) {

        this.groupeRepository = groupeRepository;
    }

    @Override
    public GroupeUtilisateur create(GroupeUtilisateur groupeUtilisateur) {
        try {
            return groupeRepository.save(groupeUtilisateur);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Erreur : le libellé du groupe doit être unique.", e);

        }
    }

    @Override
    public GroupeUtilisateur update(Long id, GroupeUtilisateur groupeUtilisateur) {
        if (groupeRepository.existsById(id)) {
            groupeUtilisateur.setLibeleGroupeUtilisateur(groupeUtilisateur.getLibeleGroupeUtilisateur());
            groupeUtilisateur.setDescriptionGroupeUtilisateur(groupeUtilisateur.getDescriptionGroupeUtilisateur());
            groupeUtilisateur.setType(groupeUtilisateur.getType());
            return groupeRepository.save(groupeUtilisateur);
        } else {
            throw new IllegalArgumentException("GroupeUtilisateur with id " + id + " does not exist.");
        }
    }

    @Override
    public List<GroupeUtilisateur> getAll() {
        return groupeRepository.findAll();
    }

    @Override
    public GroupeUtilisateur getById(Long id) {
       return groupeRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Groupe d'utilisateur not found"));
    }

    @Override
    public void delete(Long id) {
        GroupeUtilisateur domaine = getById(id);
        if (domaine == null) {
            throw new IllegalStateException("Groupe d'utilisateur not found");
        }
        groupeRepository.deleteById(id);
    }
    
}
