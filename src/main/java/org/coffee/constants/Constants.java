package org.coffee.constants;

public class Constants {
    public static final String PERSISTENCE_UNIT = "CoffeeBeanPU";
    public static final String API_BASE_URL = "http://localhost:9080/coffee-1.0-SNAPSHOT/api";

    public static class SessionAttributeKeys {
        public static final String LOGGED_IN_USER_ID = "loggedInUserId";
        public static final String LOGGED_IN_USER_ROLE = "loggedInUserRole";
        public static final String LOGGED_IN_USER_EMAIL = "loggedInUserEmail";
        public static final String LOGGED_IN_USERNAME = "loggedInUsername";
    }
}