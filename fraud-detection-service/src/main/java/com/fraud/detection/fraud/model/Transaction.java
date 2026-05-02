package com.fraud.detection.fraud.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Transaction extends PanacheEntity {
    public Long transactionId; // Reference to the ID from Transaction Service
    public Double amount;
    public String currency;
    public String merchant;
    public String cardNumber;
    public LocalDateTime timestamp;
    public String status;
    public Double riskScore;
    public String fraudExplanation;

    public Transaction() {}
}
