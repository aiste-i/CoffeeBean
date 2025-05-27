package org.coffee.socket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws/employee", configurator = EmployeeAuthConfigurator.class)
public class EmployeeWebSocketEndpoint {
    private static Set<Session> employeeSessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String employeeUsername = (String) config.getUserProperties().get("employeeUsername");

        if (employeeUsername == null) {
            System.err.println("Employee WebSocket: Unauthorized connection attempt, closing session " + session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication required."));
            } catch (IOException e) {
            }
            return;
        }

        employeeSessions.add(session);
        session.getUserProperties().put("employeeUsername", employeeUsername);

    }

    @OnMessage
    public void onMessage(String message, Session session) {}

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        employeeSessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        employeeSessions.remove(session);
    }

    public static void broadcastToEmployees(String messageJson) {
        if (messageJson == null) return;
        int count = 0;
        for (Session session : employeeSessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(messageJson);
                count++;
            }
        }
        if (count > 0) {
            System.out.println("Broadcasted to " + count + " employee(s): " +
                    (messageJson.length() > 100 ? messageJson.substring(0, 100) + "..." : messageJson));
        }
    }
}