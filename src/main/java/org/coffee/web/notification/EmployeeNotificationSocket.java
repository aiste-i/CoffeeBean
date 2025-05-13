//package org.coffee.web.notification;
//
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.enterprise.event.Observes;
//import javax.inject.Inject; // If you were to inject other services
//import javax.
//import javax.mail.Session;
//import javax.websocket.CloseReason;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//import javax.websocket.OnClose;
//
//
//@ApplicationScoped
//@ServerEndpoint("/websocket/employee/notifications")
//public class EmployeeNotificationSocket {
//
//    private static Set<Session> employeeSessions = Collections.synchronizedSet(new HashSet<>());
//    private final ObjectMapper objectMapper;
//
//    public EmployeeNotificationSocket() {
//        this.objectMapper = new ObjectMapper();
//        this.objectMapper.registerModule(new JavaTimeModule()); // Register module for Java 8+ time types
//    }
//
//    @OnOpen
//    public void onOpen(Session session) {
//        // TODO: Add security - ensure only authenticated EMPLOYEES can connect
//        String username = "Guest";
//        if (session.getUserPrincipal() != null) {
//            username = session.getUserPrincipal().getName();
//            // Example: Check for a specific role
//            // if (!session.isUserInRole("EMPLOYEE")) {
//            //     try {
//            //         System.out.println("Unauthorized WebSocket connection attempt by: " + username);
//            //         session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "User not authorized for employee notifications."));
//            //     } catch (IOException e) {
//            //         // Log or handle error closing session
//            //     }
//            //     return; // Do not add session
//            // }
//        } else {
//            // Handle unauthenticated users if your security setup allows them to reach here
//            // For employee notifications, they should ideally be authenticated.
//            try {
//                System.out.println("Unauthenticated WebSocket connection attempt. Closing.");
//                session.(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Authentication required."));
//            } catch (IOException e) { /* Log or handle */ }
//            return;
//        }
//
//        employeeSessions.add(session);
//        System.out.println("Employee WebSocket opened: " + session.getId() + ", User: " + username);
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("Message from employee " + session.getId() + ": " + message);
//        // Could be used for keep-alive (ping/pong) or other client->server commands
//        if ("ping".equalsIgnoreCase(message)) {
//            try {
//                session.getAsyncRemote().sendText("pong");
//            } catch (Exception e) {
//                System.err.println("Error sending pong to session " + session.getId() + ": " + e.getMessage());
//            }
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        employeeSessions.remove(session);
//        String username = (session.getUserPrincipal() != null) ? session.getUserPrincipal().getName() : "Unknown";
//        System.out.println("Employee WebSocket closed: " + session.getId() +
//                ", User: " + username +
//                ", Reason: " + closeReason.getReasonPhrase() +
//                " (Code: " + closeReason.getCloseCode().getCode() + ")");
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        // It's good practice to remove the session on error as well
//        employeeSessions.remove(session);
//        String username = (session.getUserPrincipal() != null) ? session.getUserPrincipal().getName() : "Unknown";
//        System.err.println("Error on employee WebSocket " + session.getId() +
//                ", User: " + username +
//                ": " + throwable.getMessage());
//        // For debugging, print stack trace, but avoid in production logs unless needed
//        // throwable.printStackTrace();
//    }
//
//    public void onNewOrderForEmployee(@Observes NewOrderForEmployeeEvent event) {
//        Order newOrder = event.getNewOrder();
//        System.out.println("EmployeeNotificationSocket observed NewOrderForEmployeeEvent for Order ID: " + newOrder.getId());
//
//        try {
//            OrderNotificationPayload payload = new OrderNotificationPayload(
//                    newOrder.getId(),
//                    newOrder.getCustomerName() != null ? newOrder.getCustomerName() : "N/A",
//                    newOrder.getTotalPrice(),
//                    newOrder.getItems() != null ? newOrder.getItems().size() : 0,
//                    newOrder.getCreated()
//            );
//            String jsonPayload = objectMapper.writeValueAsString(payload);
//            broadcastToEmployees(jsonPayload);
//        } catch (Exception e) {
//            System.err.println("Error serializing or broadcasting new order notification: " + e.getMessage());
//            e.printStackTrace(); // For detailed error during development
//        }
//    }
//
//    private void broadcastToEmployees(String message) {
//        // Iterate over a copy to avoid ConcurrentModificationException if sessions are modified during broadcast
//        Set<Session> sessionsToBroadcastTo;
//        synchronized (employeeSessions) {
//            sessionsToBroadcastTo = new HashSet<>(employeeSessions);
//        }
//
//        System.out.println("Broadcasting to " + sessionsToBroadcastTo.size() + " employee sessions.");
//        for (Session session : sessionsToBroadcastTo) {
//            if (session.isOpen()) {
//                try {
//                    // Use asynchronous send to avoid blocking the event observer thread
//                    session.getAsyncRemote().sendText(message);
//                } catch (IllegalStateException e) {
//                    // This can happen if the session is closed concurrently
//                    System.err.println("Attempted to send to a closed session " + session.getId() + ": " + e.getMessage());
//                    // Optionally, try to remove it again if it wasn't caught by onClose/onError
//                    employeeSessions.remove(session);
//                }
//                catch (Exception e) {
//                    System.err.println("Failed to send message to employee session " + session.getId() + ": " + e.getMessage());
//                    // Consider more robust error handling or session cleanup here
//                }
//            } else {
//                // Session is not open, remove it
//                System.out.println("Session " + session.getId() + " is not open. Removing.");
//                employeeSessions.remove(session);
//            }
//        }
//    }