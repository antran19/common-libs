package com.nexus.common.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexus.common.core.exception.DomainException;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

// Registered in META-INF/spring/...AutoConfiguration.imports, so it is applied after Boot's own
// Kafka auto-configuration and only when that one actually created a ConsumerFactory (tests that
// exclude KafkaAutoConfiguration get none of this). Being listed there also keeps it out of the
// services' "com.nexus" component scan -- which is also why there are no nested @Configuration
// classes here: those WOULD be scanned on their own and ignore the conditions above.
@AutoConfiguration(after = KafkaAutoConfiguration.class)
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnBean(ConsumerFactory.class)
public class NexusKafkaAutoConfiguration {

    public static final int MAX_RETRIES = 3;
    static final long INITIAL_INTERVAL_MS = 1000;
    static final double MULTIPLIER = 2.0;

    // What happens when a @KafkaListener throws. Spring's default retries 9 times back-to-back,
    // then just logs and skips the record -- lost for good. Here: retry 3 times waiting 1s, 2s,
    // 4s (enough for a DB/network blip), then copy the record to this service's own DLT
    // (see DeadLetterTopics) and move on, so one bad record doesn't block the partition. Broken
    // JSON or a business-rule rejection (DomainException) fails the same way every time, so
    // those skip the retries and go straight to the DLT. Boot applies this bean to every listener.
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(ObjectProvider<KafkaTemplate<String, String>> kafkaTemplate,
                                                 @Value("${spring.application.name}") String applicationName) {
        // KafkaTemplate is looked up on the first failure, not at startup. Partition -1 = let
        // Kafka pick, since the auto-created DLT may have fewer partitions than the source.
        ConsumerRecordRecoverer deadLetter = (record, ex) -> new DeadLetterPublishingRecoverer(
                kafkaTemplate.getObject(),
                (r, e) -> new TopicPartition(DeadLetterTopics.forSource(r.topic(), applicationName), -1))
                .accept(record, ex);

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(MAX_RETRIES);
        backOff.setInitialInterval(INITIAL_INTERVAL_MS);
        backOff.setMultiplier(MULTIPLIER);

        DefaultErrorHandler handler = new DefaultErrorHandler(deadLetter, backOff);
        handler.addNotRetryableExceptions(JsonProcessingException.class, DomainException.class);
        return handler;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public DeadLetterService deadLetterService(ConsumerFactory<?, ?> consumerFactory,
                                               ObjectProvider<KafkaListenerEndpointRegistry> registry,
                                               @Value("${spring.application.name}") String applicationName) {
        // Every service in this project configures String key/value deserializers.
        return new DeadLetterService((ConsumerFactory<String, String>) consumerFactory, registry, applicationName);
    }

    @Bean
    public DeadLetterMonitor deadLetterMonitor(DeadLetterService deadLetterService,
                                               ObjectProvider<MeterRegistry> meterRegistry) {
        return new DeadLetterMonitor(deadLetterService, meterRegistry.getIfAvailable());
    }
}
