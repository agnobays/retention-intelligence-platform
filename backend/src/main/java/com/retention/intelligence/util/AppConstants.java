package com.retention.intelligence.util;

public class AppConstants {

    public static final String API_V1_PREFIX = "/api/v1";

    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String AT_RISK = "AT_RISK";
        public static final String RECOVERING = "RECOVERING";
        public static final String CHURNED = "CHURNED";
        public static final String SAVED = "SAVED";
    }

    public static class Workflow {
        public static final String PROCESS_KEY_RECOVERY = "CustomerRecoveryProcess";
    }
}
