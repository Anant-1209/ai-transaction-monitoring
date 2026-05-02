package com.fraud.detection.notification.consumer;

import com.fraud.detection.notification.model.FraudAlert;
import com.fraud.detection.notification.websocket.AlertSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AlertConsumer {

    private static final Logger LOG = Logger.getLogger(AlertConsumer.class);

    @Inject
    AlertSocket socket;

    @Incoming("fraud-alerts-in")
    public void consume(FraudAlert alert) {
        LOG.info("Received Fraud Alert from Kafka for transaction: " + alert.transactionId);
        
        // Push to WebSocket
        socket.broadcast(alert);
        
        LOG.info("Alert broadcasted to all dashboards.");
    }
}
