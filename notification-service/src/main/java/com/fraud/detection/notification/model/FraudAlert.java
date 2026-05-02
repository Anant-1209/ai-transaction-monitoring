package com.fraud.detection.notification.model;

public class FraudAlert {
    public Long transactionId;
    public Double amount;
    public String merchant;
    public Double riskScore;
    public String status;
    public String fraudExplanation;

    public FraudAlert() {}
}
