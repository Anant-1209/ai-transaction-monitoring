package com.fraud.detection.transaction.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Entity
public class Transaction extends PanacheEntity {
    public Double amount;
    public String currency;
    public String merchant;
    public String cardNumber;
    public LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    public TransactionStatus status;
    
    public Double riskScore;
    public String customerId;

    public Transaction() {
        this.timestamp = java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Kolkata"));
        this.status = TransactionStatus.PENDING;
    }
}
