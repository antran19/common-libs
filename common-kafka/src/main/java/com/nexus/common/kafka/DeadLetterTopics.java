package com.nexus.common.kafka;

// Each service gets its OWN dead-letter topic per source topic, e.g.
// "commerce-events.fulfillment-service.DLT". Several services read the same source topic; with a
// shared "<topic>.DLT", replaying a record would mean re-publishing it where EVERY consumer sees
// it again (fulfillment restocking twice for an event only auction failed on). Per-service
// names keep the failures, the backlog count and the replay scoped to the service that failed.
public final class DeadLetterTopics {

    public static final String SUFFIX = ".DLT";

    private DeadLetterTopics() {
    }

    public static String forSource(String sourceTopic, String applicationName) {
        return sourceTopic + "." + applicationName + SUFFIX;
    }
}
