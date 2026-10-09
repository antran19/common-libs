package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetRequestedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent("user-1", "raw-reset-token");

        String json = mapper.writeValueAsString(event);
        PasswordResetRequestedEvent parsed = mapper.readValue(json, PasswordResetRequestedEvent.class);

        assertThat(parsed.getUserId()).isEqualTo("user-1");
        assertThat(parsed.getResetToken()).isEqualTo("raw-reset-token");
        assertThat(parsed.getEventType()).isEqualTo("PasswordResetRequested");
        assertThat(parsed.getAggregateId()).isEqualTo("user-1");
    }
}
