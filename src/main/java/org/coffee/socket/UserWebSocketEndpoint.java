package org.coffee.socket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint("/ws/user/{userId}")
public class UserWebSocketEndpoint {
    private static Map<String, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(final Session session, @PathParam("userId") String userId) {
        System.out.println("User WebSocket connected: " + userId + ", Session ID: " + session.getId());
        userSessions.put(userId, session);
        session.getUserProperties().put("userId", userId);
    }

    @OnMessage
    public void onMessage(final Session session, @PathParam("userId") String userId, String message) {

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String userId = (String) session.getUserProperties().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("User WebSocket disconnected: " + userId + ", Reason: " + closeReason.getReasonPhrase());
        } else {
            System.out.println("User WebSocket session " + session.getId() + " disconnected without userId property.");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String userId = (String) session.getUserProperties().get("userId");
        if (userId != null)
            userSessions.remove(userId);
    }

    public static void sendToUser(String userId, String messageJson) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(messageJson);
            } catch (IOException e) {
                 userSessions.remove(userId);
            }
        }
    }
}
