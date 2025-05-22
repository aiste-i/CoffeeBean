// src/main/webapp/resources/js/websockets.js

let userSocketConnection;    // Renamed to avoid conflict with p:socket if used elsewhere
let employeeSocketConnection; // Renamed

const APP_CONTEXT_PATH = '/coffee-1.0-SNAPSHOT'; // IMPORTANT: SET YOUR ACTUAL APPLICATION CONTEXT PATH!

// --- User WebSocket ---
function connectUserWebSocket(userId) {
    if (!userId) {
        console.warn("UserWS: No userId provided, cannot connect.");
        return;
    }
    if (userSocketConnection && (userSocketConnection.readyState === WebSocket.OPEN || userSocketConnection.readyState === WebSocket.CONNECTING)) {
        console.log(`UserWS for ${userId}: Already connected or connecting.`);
        return;
    }

    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const userWsUrl = `${wsProtocol}//${window.location.host}${APP_CONTEXT_PATH}/ws/user/${userId}`;

    console.log(`UserWS: Attempting to connect to ${userWsUrl}`);
    try {
        userSocketConnection = new WebSocket(userWsUrl);
    } catch (e) {
        console.error(`UserWS: Error creating WebSocket object for ${userWsUrl}:`, e);
        return;
    }


    userSocketConnection.onopen = function(event) {
        console.log(`UserWS connected for user ID: ${userId}`);
    };

    userSocketConnection.onmessage = function(event) {
        console.log(`UserWS (generic handler) received for ${userId}: ${event.data.substring(0,150)}...`);
        try {
            const notification = JSON.parse(event.data);
            if (typeof window.handleUserOrderNotification === 'function') {
                window.handleUserOrderNotification(notification);
            } else {
                console.warn(`UserWS: Global 'window.handleUserOrderNotification' function not found on page.`);
                // Default action: alert("User Notification: " + notification.type);
            }
        } catch (e) {
            console.error(`UserWS: Error parsing message for ${userId}:`, e, "Raw data:", event.data);
        }
    };

    userSocketConnection.onclose = function(event) {
        console.log(`UserWS disconnected for ${userId}. Code: ${event.code}, Reason: "${event.reason}", Clean: ${event.wasClean}`);
        userSocketConnection = null; // Clear reference
        // Optional: Reconnect logic for user
        // if (!event.wasClean && userId) { setTimeout(() => connectUserWebSocket(userId), 5000); }
    };

    userSocketConnection.onerror = function(error) {
        console.error(`UserWS error for ${userId}: `, error);
        // onerror is usually followed by onclose.
    };
}

// --- Employee WebSocket ---
function connectEmployeeWebSocket() {
    if (employeeSocketConnection && (employeeSocketConnection.readyState === WebSocket.OPEN || employeeSocketConnection.readyState === WebSocket.CONNECTING)) {
        console.log('EmployeeWS: Already connected or connecting.');
        return;
    }

    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const employeeWsUrl = `${wsProtocol}//${window.location.host}${APP_CONTEXT_PATH}/ws/employee`;

    console.log(`EmployeeWS: Attempting to connect to ${employeeWsUrl}`);
    try {
        employeeSocketConnection = new WebSocket(employeeWsUrl);
    } catch (e) {
        console.error(`EmployeeWS: Error creating WebSocket object for ${employeeWsUrl}:`, e);
        return;
    }

    employeeSocketConnection.onopen = function(event) {
        console.log('EmployeeWS connected.');
    };

    employeeSocketConnection.onmessage = function(event) {
        console.log(`EmployeeWS (generic handler) received: ${event.data.substring(0,150)}...`);
        try {
            const notification = JSON.parse(event.data);
            if (typeof window.handleEmployeeOrderNotification === 'function') {
                window.handleEmployeeOrderNotification(notification);
            } else {
                console.warn('EmployeeWS: Global function not found on page.');
                // Default action: alert("Employee Notification: " + notification.type);
            }
        } catch (e) {
            console.error('EmployeeWS: Error parsing message:', e, "Raw data:", event.data);
        }
    };

    employeeSocketConnection.onclose = function(event) {
        console.log(`EmployeeWS disconnected. Code: ${event.code}, Reason: "${event.reason}", Clean: ${event.wasClean}`);
        employeeSocketConnection = null; // Clear reference
        // Optional: More robust reconnect logic for employees
        if (!event.wasClean) { // e.g. server restart, network drop
            console.log("EmployeeWS: Attempting to reconnect in 5 seconds...");
            setTimeout(connectEmployeeWebSocket, 5000);
        }
    };

    employeeSocketConnection.onerror = function(error) {
        console.error('EmployeeWS error: ', error);
    };
}
