package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryLevelChangedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip_keyedByProduct() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InventoryLevelChangedEvent event = new InventoryLevelChangedEvent("product-1", 7);

        InventoryLevelChangedEvent parsed =
                mapper.readValue(mapper.writeValueAsString(event), InventoryLevelChangedEvent.class);

        assertThat(parsed.getProductId()).isEqualTo("product-1");
        assertThat(parsed.getAvailableQuantity()).isEqualTo(7);
        assertThat(parsed.getEventType()).isEqualTo("InventoryLevelChanged");
        assertThat(parsed.getAggregateId()).isEqualTo("product-1");
        assertThat(parsed.getOccurredAt()).isEqualTo(event.getOccurredAt());
    }
}
