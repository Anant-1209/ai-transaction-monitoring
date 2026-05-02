package com.fraud.detection.fraud.consumer;

import com.fraud.detection.fraud.logic.FraudDetectionEngine;
import com.fraud.detection.fraud.logic.FraudExplanationService;
import com.fraud.detection.fraud.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TransactionConsumer {

    private static final Logger LOG = Logger.getLogger(TransactionConsumer.class);

    @Inject
    FraudDetectionEngine engine;

    @Inject
    FraudExplanationService aiService;

    @Incoming("transactions-in")
    @Outgoing("fraud-alerts")
    @Transactional
    public Transaction process(Transaction transaction) {
        LOG.info("Received transaction for analysis: " + transaction.amount);

        // Calculate risk
        double score = engine.calculateRiskScore(transaction);
        transaction.riskScore = score;
        
        // Always generate AI explanation for testing/demo purposes
        try {
            transaction.fraudExplanation = aiService.explainFraud(transaction);
            LOG.info("AI Explanation generated: " + transaction.fraudExplanation);
        } catch (Exception e) {
            LOG.error("Failed to generate AI explanation", e);
            transaction.fraudExplanation = "AI analysis currently unavailable.";
        }

        if (score > 0.5) {
            transaction.status = "FRAUD_FLAGGED";
        } else {
            transaction.status = "COMPLETED";
        }

        // Save result (use merge because the entity already has an ID from the transaction service)
        Transaction.getEntityManager().merge(transaction);

        LOG.info("Analysis complete. Risk Score: " + score);
        return transaction; // Sent to fraud-alerts topic
    }
}
