package com.fraud.detection.fraud.consumer;

import com.fraud.detection.fraud.model.Transaction;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class TransactionDeserializer extends ObjectMapperDeserializer<Transaction> {
    public TransactionDeserializer() {
        super(Transaction.class);
    }
}
