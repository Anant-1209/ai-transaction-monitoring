package com.fraud.detection.notification.websocket;

import com.fraud.detection.notification.model.FraudAlert;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

@WebSocket(path = "/alerts")
@ApplicationScoped
public class AlertSocket {

    private static final Logger LOG = Logger.getLogger(AlertSocket.class);

    // Manual list of active dashboard connections
    private static final Set<WebSocketConnection> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.info("New dashboard connected to alerts socket");
        SESSIONS.add(connection);
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        LOG.info("Dashboard disconnected");
        SESSIONS.remove(connection);
    }

    public void broadcast(FraudAlert alert) {
        LOG.info("Broadcasting alert to " + SESSIONS.size() + " dashboard(s)");
        for (WebSocketConnection session : SESSIONS) {
            try {
                session.sendTextAndAwait(alert);
            } catch (Exception e) {
                LOG.error("Failed to send message to a session, removing it.", e);
                SESSIONS.remove(session);
            }
        }
    }
}
