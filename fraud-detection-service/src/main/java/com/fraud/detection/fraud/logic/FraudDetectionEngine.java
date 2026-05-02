package com.fraud.detection.fraud.logic;

import com.fraud.detection.fraud.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FraudDetectionEngine {

    public Double calculateRiskScore(Transaction transaction) {
        double score = 0.0;

        // Rule 1: High Amount (Over 10k)
        if (transaction.amount > 10000) {
            score += 0.4;
        }
        
        // Rule 1b: Extreme Amount (Over 100k) - Instant High Risk
        if (transaction.amount > 100000) {
            score += 0.5;
        }

        // Rule 2: Specific Merchant (Simulating a blacklisted merchant)
        if ("SuspectShop".equalsIgnoreCase(transaction.merchant)) {
            score += 0.5;
        }

        // Rule 3: Basic ML Anomaly (Heuristic)
        // In a real app, this would call a PMML model or a Python microservice
        if (isAnomaly(transaction)) {
            score += 0.3;
        }

        return Math.min(score, 1.0); // Max score is 1.0
    }

    private boolean isAnomaly(Transaction t) {
        // Simple logic: if amount has too many decimals or specific pattern
        return t.amount % 1 != 0 && t.amount > 5000;
    }
}
