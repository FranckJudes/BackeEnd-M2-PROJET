package com.harmony.harmoniservices.core.cases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.harmony.harmoniservices.core.domain.entities.Gateway;
import com.harmony.harmoniservices.core.domain.entities.SequenceFlow;
import com.harmony.harmoniservices.core.domain.services.GatewayEvaluator;

import lombok.extern.slf4j.Slf4j;

/**
 * Implémentation du service d'évaluation des passerelles BPMN
 */
@Service
@Slf4j
public class GatewayEvaluatorImpl implements GatewayEvaluator {

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public SequenceFlow evaluateExclusiveGateway(Gateway gateway, List<SequenceFlow> outgoingFlows, Map<String, Object> variables) {
        log.debug("Évaluation de la passerelle exclusive {}", gateway.getId());
        
        // Chercher d'abord un flux par défaut (sans condition)
        Optional<SequenceFlow> defaultFlow = outgoingFlows.stream()
                .filter(flow -> flow.getConditionExpression() == null || flow.getConditionExpression().isEmpty())
                .findFirst();
        
        // Évaluer les conditions de chaque flux sortant
        for (SequenceFlow flow : outgoingFlows) {
            if (flow.getConditionExpression() != null && !flow.getConditionExpression().isEmpty()) {
                if (evaluateCondition(flow, variables)) {
                    log.debug("Condition satisfaite pour le flux {}", flow.getId());
                    return flow;
                }
            }
        }
        
        // Si aucune condition n'est satisfaite, retourner le flux par défaut s'il existe
        if (defaultFlow.isPresent()) {
            log.debug("Aucune condition satisfaite, utilisation du flux par défaut {}", defaultFlow.get().getId());
            return defaultFlow.get();
        }
        
        // Si aucun flux par défaut n'existe et qu'aucune condition n'est satisfaite, lancer une exception
        throw new IllegalStateException(
                "Aucune condition satisfaite pour la passerelle exclusive " + gateway.getId() + " et pas de flux par défaut");
    }

    @Override
    public List<SequenceFlow> evaluateInclusiveGateway(Gateway gateway, List<SequenceFlow> outgoingFlows, Map<String, Object> variables) {
        log.debug("Évaluation de la passerelle inclusive {}", gateway.getId());
        
        List<SequenceFlow> selectedFlows = new ArrayList<>();
        Optional<SequenceFlow> defaultFlow = outgoingFlows.stream()
                .filter(flow -> flow.getConditionExpression() == null || flow.getConditionExpression().isEmpty())
                .findFirst();
        
        // Évaluer les conditions de chaque flux sortant
        for (SequenceFlow flow : outgoingFlows) {
            if (flow.getConditionExpression() != null && !flow.getConditionExpression().isEmpty()) {
                if (evaluateCondition(flow, variables)) {
                    log.debug("Condition satisfaite pour le flux {}", flow.getId());
                    selectedFlows.add(flow);
                }
            }
        }
        
        // Si aucune condition n'est satisfaite, utiliser le flux par défaut s'il existe
        if (selectedFlows.isEmpty() && defaultFlow.isPresent()) {
            log.debug("Aucune condition satisfaite, utilisation du flux par défaut {}", defaultFlow.get().getId());
            selectedFlows.add(defaultFlow.get());
        }
        
        // Si aucun flux n'est sélectionné et qu'il n'y a pas de flux par défaut, lancer une exception
        if (selectedFlows.isEmpty()) {
            throw new IllegalStateException(
                    "Aucune condition satisfaite pour la passerelle inclusive " + gateway.getId() + " et pas de flux par défaut");
        }
        
        return selectedFlows;
    }

    @Override
    public List<SequenceFlow> evaluateParallelGateway(Gateway gateway, List<SequenceFlow> outgoingFlows) {
        log.debug("Évaluation de la passerelle parallèle {}", gateway.getId());
        
        // Pour une passerelle parallèle, tous les flux sortants sont activés
        return new ArrayList<>(outgoingFlows);
    }

    @Override
    public boolean evaluateCondition(SequenceFlow sequenceFlow, Map<String, Object> variables) {
        String conditionExpression = sequenceFlow.getConditionExpression();
        
        if (conditionExpression == null || conditionExpression.isEmpty()) {
            // Si pas de condition, considérer comme toujours vrai
            return true;
        }
        
        try {
            // Préparer le contexte d'évaluation avec les variables du processus
            EvaluationContext context = new StandardEvaluationContext();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
            
            // Nettoyer l'expression si nécessaire (enlever les préfixes JUEL ${} ou autres)
            String cleanExpression = cleanExpression(conditionExpression);
            
            // Parser et évaluer l'expression
            Expression expression = expressionParser.parseExpression(cleanExpression);
            Boolean result = expression.getValue(context, Boolean.class);
            
            return result != null && result;
        } catch (Exception e) {
            log.error("Erreur lors de l'évaluation de la condition '{}' pour le flux {}: {}", 
                    conditionExpression, sequenceFlow.getId(), e.getMessage());
            return false;
        }
    }
    
    /**
     * Nettoie une expression de condition pour la rendre compatible avec SpEL
     * @param expression Expression à nettoyer
     * @return Expression nettoyée
     */
    private String cleanExpression(String expression) {
        // Enlever les délimiteurs JUEL ${...}
        if (expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length() - 1);
        }
        
        // Remplacer les opérateurs JUEL par des opérateurs SpEL si nécessaire
        expression = expression.replace(" eq ", " == ")
                              .replace(" ne ", " != ")
                              .replace(" lt ", " < ")
                              .replace(" le ", " <= ")
                              .replace(" gt ", " > ")
                              .replace(" ge ", " >= ")
                              .replace(" and ", " && ")
                              .replace(" or ", " || ");
        
        return expression;
    }
} 