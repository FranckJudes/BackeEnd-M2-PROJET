package com.harmony.harmoniservices.core.domain.entities;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GroupeUtilisateur {

    private Long id;
    private String libeleGroupeUtilisateur;
    private String descriptionGroupeUtilisateur;
    private String type;
    private String createdAt;
    private String updatedAt;


}
