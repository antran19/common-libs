package com.nexus.common.kafka;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Once a minute: how many dead letters are waiting? Anything above zero is logged at WARN (so it
// shows up in Dozzle) and exposed as the gauge "nexus.kafka.dlt.backlog{topic=...}".
// Deliberately NOT a health indicator: reporting DOWN could get the whole service treated as
// dead and taken out of rotation over a few old records that need a human to look at them.
public class DeadLetterMonitor {

    static final String METRIC = "nexus.kafka.dlt.backlog";
    private static final Logger log = LoggerFactory.getLogger(DeadLetterMonitor.class);

    private final DeadLetterService deadLetterService;
    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicLong> gauges = new ConcurrentHashMap<>();

    // meterRegistry may be null (no actuator/micrometer): then only the log line is produced.
    public DeadLetterMonitor(DeadLetterService deadLetterService, MeterRegistry meterRegistry) {
        this.deadLetterService = deadLetterService;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(initialDelayString = "${nexus.kafka.dlt.check-initial-delay-ms:30000}",
               fixedDelayString = "${nexus.kafka.dlt.check-interval-ms:60000}")
    public void check() {
        Map<String, Long> backlog;
        try {
            backlog = deadLetterService.backlog();
        } catch (Exception e) {
            log.warn("Could not check dead-letter backlog: {}", e.getMessage());
            return;
        }
        backlog.forEach((topic, count) -> {
            gauge(topic).set(count);
            if (count > 0) {
                log.warn("{} event(s) waiting in dead-letter topic {} -- see GET /internal/dlt, "
                        + "fix the cause, then POST /internal/dlt/replay", count, topic);
            }
        });
    }

    long lastValue(String topic) {
        AtomicLong value = gauges.get(topic);
        return value == null ? 0 : value.get();
    }

    private AtomicLong gauge(String topic) {
        return gauges.computeIfAbsent(topic, t -> {
            AtomicLong value = new AtomicLong();
            if (meterRegistry != null) {
                Gauge.builder(METRIC, value, AtomicLong::get)
                        .tag("topic", t)
                        .description("Dead letters waiting to be replayed")
                        .register(meterRegistry);
            }
            return value;
        });
    }
}
