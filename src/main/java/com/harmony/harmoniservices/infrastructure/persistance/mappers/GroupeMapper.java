package com.harmony.harmoniservices.infrastructure.persistance.mappers;


import com.harmony.harmoniservices.core.domain.entities.GroupeUtilisateur;
import com.harmony.harmoniservices.infrastructure.persistance.entitites.GroupeEntity;
import com.harmony.harmoniservices.infrastructure.persistance.enums.TypeGroupe;

public class GroupeMapper {

    public static GroupeUtilisateur toDomain(GroupeEntity entity) {
        if (entity == null) {
            return null;
        }
        GroupeUtilisateur domain = new GroupeUtilisateur();
        domain.setId(entity.getId());
        domain.setLibeleGroupeUtilisateur(entity.getLibele_groupe_utilisateur());
        domain.setType(entity.getType() != null ? entity.getType().name() : null);
        domain.setDescriptionGroupeUtilisateur(entity.getDescription_groupe_utilisateur());
        domain.setCreatedAt(entity.getCreated_at() != null ? entity.getCreated_at().toString() : null);
        domain.setUpdatedAt(entity.getUpdated_at() != null ? entity.getUpdated_at().toString() : null);
        return domain;
    }

    public static GroupeEntity toEntity(GroupeUtilisateur domain) {
        if (domain == null) {
            return null;
        }
        return GroupeEntity.builder()
                .id(domain.getId())
                .libele_groupe_utilisateur(domain.getLibeleGroupeUtilisateur())
                .type(domain.getType() != null ? TypeGroupe.valueOf(domain.getType()) : TypeGroupe.TYPE_0)
                .description_groupe_utilisateur(domain.getDescriptionGroupeUtilisateur())
                .build();
    }
}