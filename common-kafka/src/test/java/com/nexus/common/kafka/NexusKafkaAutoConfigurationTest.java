package com.nexus.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NexusKafkaAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withPropertyValues("spring.application.name=test-service");

    @Test
    void withKafka_registersErrorHandlerReplayAndMonitor() {
        runner.withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class, NexusKafkaAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DefaultErrorHandler.class);
                    assertThat(context).hasSingleBean(DeadLetterService.class);
                    assertThat(context).hasSingleBean(DeadLetterMonitor.class);
                    assertThat(context.getBean(DeadLetterService.class).replayGroupId())
                            .isEqualTo("test-service-dlt-replay");
                });
    }

    // Tests in the services exclude KafkaAutoConfiguration; they must still start.
    @Test
    void withoutKafkaAutoConfiguration_registersNothing() {
        runner.withConfiguration(AutoConfigurations.of(NexusKafkaAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(DefaultErrorHandler.class);
                    assertThat(context).doesNotHaveBean(DeadLetterService.class);
                });
    }

    @Test
    void deadLetterTopic_isScopedToTheService() {
        assertThat(DeadLetterTopics.forSource("commerce-events", "fulfillment-service"))
                .isEqualTo("commerce-events.fulfillment-service.DLT");
    }

    @Test
    void invoke_usesTheThreeArgumentFormThatListenerAdaptersImplement() {
        List<String> seen = new ArrayList<>();
        AcknowledgingConsumerAwareMessageListener<String, String> adapterLike =
                (record, ack, consumer) -> seen.add(record.topic() + ":" + record.value());

        DeadLetterService.invoke(adapterLike, new ConsumerRecord<>("auction-events", 0, 7L, "k", "{\"a\":1}"));

        assertThat(seen).containsExactly("auction-events:{\"a\":1}");
    }

    @Test
    void invoke_propagatesListenerFailure() {
        AcknowledgingConsumerAwareMessageListener<String, String> failing = (record, ack, consumer) -> {
            throw new ListenerExecutionFailedException("wrapped", new IllegalStateException("db down"));
        };

        assertThatThrownBy(() -> DeadLetterService.invoke(failing, new ConsumerRecord<>("t", 0, 0L, "k", "v")))
                .isInstanceOf(ListenerExecutionFailedException.class);
    }

    @Test
    void rootMessage_unwrapsToTheRealCause() {
        Exception wrapped = new ListenerExecutionFailedException("wrapped", new IllegalStateException("db down"));

        assertThat(DeadLetterService.rootMessage(wrapped)).isEqualTo("IllegalStateException: db down");
    }
}
