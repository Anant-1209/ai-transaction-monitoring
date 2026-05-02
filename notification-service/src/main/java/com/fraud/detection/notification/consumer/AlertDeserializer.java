package com.fraud.detection.notification.consumer;

import com.fraud.detection.notification.model.FraudAlert;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class AlertDeserializer extends ObjectMapperDeserializer<FraudAlert> {
    public AlertDeserializer() {
        super(FraudAlert.class);
    }
}
