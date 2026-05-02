package com.fraud.detection.fraud.logic;

import com.fraud.detection.fraud.model.Transaction;
import io.quarkiverse.langchain4j.RegisterAiService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService
public interface FraudExplanationService {

    @SystemMessage("You are a senior fraud analyst. Analyze the provided transaction and provide a concise, professional explanation of why it was flagged as suspicious based on the risk score.")
    @UserMessage("Transaction Details: Amount: {transaction.amount}, Merchant: {transaction.merchant}, Risk Score: {transaction.riskScore}. Explain the risk.")
    String explainFraud(Transaction transaction);
}
