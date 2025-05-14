package com.harmony.harmoniservices.core.domain.services;

import java.util.List;
import java.util.Map;

import com.harmony.harmoniservices.core.domain.entities.Gateway;
import com.harmony.harmoniservices.core.domain.entities.SequenceFlow;

/**
 * Service pour évaluer les passerelles BPMN et déterminer les chemins à suivre
 */
public interface GatewayEvaluator {
    
    /**
     * Évalue une passerelle exclusive (XOR) et retourne la séquence à suivre
     * @param gateway Passerelle à évaluer
     * @param outgoingFlows Flux sortants de la passerelle
     * @param variables Variables du processus pour évaluer les conditions
     * @return La séquence à suivre (une seule pour XOR)
     */
    SequenceFlow evaluateExclusiveGateway(Gateway gateway, List<SequenceFlow> outgoingFlows, Map<String, Object> variables);
    
    /**
     * Évalue une passerelle inclusive (OR) et retourne les séquences à suivre
     * @param gateway Passerelle à évaluer
     * @param outgoingFlows Flux sortants de la passerelle
     * @param variables Variables du processus pour évaluer les conditions
     * @return Les séquences à suivre (une ou plusieurs pour OR)
     */
    List<SequenceFlow> evaluateInclusiveGateway(Gateway gateway, List<SequenceFlow> outgoingFlows, Map<String, Object> variables);
    
    /**
     * Évalue une passerelle parallèle (AND) et retourne les séquences à suivre
     * @param gateway Passerelle à évaluer
     * @param outgoingFlows Flux sortants de la passerelle
     * @return Toutes les séquences sortantes (toutes pour AND)
     */
    List<SequenceFlow> evaluateParallelGateway(Gateway gateway, List<SequenceFlow> outgoingFlows);
    
    /**
     * Évalue une condition sur une séquence
     * @param sequenceFlow Séquence à évaluer
     * @param variables Variables du processus
     * @return true si la condition est satisfaite, false sinon
     */
    boolean evaluateCondition(SequenceFlow sequenceFlow, Map<String, Object> variables);
} 