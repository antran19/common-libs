package com.nexus.common.kafka;

import com.nexus.common.core.ApiResponse;
import com.nexus.common.core.exception.NotFoundException;
import com.nexus.common.security.RequiresPrivilege;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Operator endpoint, present in every service that consumes Kafka. Not routed by the gateway:
// call the service's own port (e.g. localhost:8086/internal/dlt) with an admin JWT.
//
// Picked up by the services' component scan of "com.nexus" (a @Controller has to be, Spring MVC
// only maps @Controller beans), so it must also work where Kafka is switched off -- e.g. tests
// that exclude KafkaAutoConfiguration: DeadLetterService is then simply absent and this answers 404.
@RestController
@RequestMapping("/internal/dlt")
public class DeadLetterController {

    public static final String PRIVILEGE = "SYSTEM.DLT.MANAGE";

    private final ObjectProvider<DeadLetterService> deadLetterService;

    public DeadLetterController(ObjectProvider<DeadLetterService> deadLetterService) {
        this.deadLetterService = deadLetterService;
    }

    @RequiresPrivilege(PRIVILEGE)
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeadLetterService.TopicStatus>>> status(
            @RequestParam(defaultValue = "5") int peek) {
        return ResponseEntity.ok(ApiResponse.ok(service().status(Math.max(0, Math.min(peek, 50)))));
    }

    // Run this AFTER fixing whatever made the records fail, otherwise they just fail again.
    @RequiresPrivilege(PRIVILEGE)
    @PostMapping("/replay")
    public ResponseEntity<ApiResponse<DeadLetterService.ReplayResult>> replay(
            @RequestParam(defaultValue = "100") int max) {
        return ResponseEntity.ok(ApiResponse.ok(service().replay(Math.max(1, Math.min(max, 1000)))));
    }

    // For a record that can never be processed and keeps stopping /replay. It stays in the topic;
    // only the "handled" pointer moves, and the dropped payload is logged (see DeadLetterService.skip).
    @RequiresPrivilege(PRIVILEGE)
    @PostMapping("/skip")
    public ResponseEntity<ApiResponse<DeadLetterService.SkipResult>> skip(
            @RequestParam String topic, @RequestParam(defaultValue = "1") int count) {
        return ResponseEntity.ok(ApiResponse.ok(service().skip(topic, Math.max(1, Math.min(count, 100)))));
    }

    private DeadLetterService service() {
        DeadLetterService service = deadLetterService.getIfAvailable();
        if (service == null) {
            throw new NotFoundException("KAFKA_NOT_CONFIGURED", "This service has no Kafka listeners configured");
        }
        return service;
    }
}
