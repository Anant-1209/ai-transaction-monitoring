package com.fraud.detection.notification.websocket;

import com.fraud.detection.notification.model.FraudAlert;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@WebSocket(path = "/alerts")
public class AlertSocket {

    private static final Logger LOG = Logger.getLogger(AlertSocket.class);

    @Inject
    WebSocketConnection connection;

    @OnOpen
    public void onOpen() {
        LOG.info("New dashboard connected to alerts socket");
    }

    public void broadcast(FraudAlert alert) {
        // Send the alert to ALL connected dashboards
        connection.broadcast().sendTextAndAwait(alert);
    }
}
