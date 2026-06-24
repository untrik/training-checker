package edu.rutmiit.grpcenrichment.config;

import edu.rutmiit.training_events.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Конфигурация RabbitMQ для enrichment-клиента.
 *
 * Этот сервис одновременно и consumer,
 * и publisher. Поэтому конфигурация включает:
 * - Exchange (общий для всех сервисов)
 * - Очередь для приёма workout.finished
 * - DLQ для необработанных сообщений
 * - RabbitTemplate для публикации workout.enriched
 *
 * Каждый consumer определяет свою очередь и привязку.
 * Enrichment-клиент слушает только workout.finished (не все события),
 * поэтому binding key = "workout.finished", а не "#".
 */
@Configuration
public class RabbitMQConfig {
    public static final String ENRICHMENT_QUEUE = "q.enrichment.workout.finished";
    public static final String ENRICHMENT_DLQ = "q.enrichment.workout.finished.dlq";


    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
    // RabbitTemplate для публикации событий
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
    // Listener Container Factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        factory.setDefaultRequeueRejected(false); // при ошибке → DLQ
        return factory;
    }
    // Exchange
    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder
                .topicExchange(RoutingKeys.EXCHANGE)
                .durable(true)
                .build();
    }
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(RoutingKeys.EXCHANGE + ".dlx")
                .durable(true)
                .build();
    }
    // Только события workout.finished — binding key "workout.finished" (не "#")
    @Bean
    public Queue enrichmentQueue() {
        return QueueBuilder
                .durable(ENRICHMENT_QUEUE)
                .deadLetterExchange(RoutingKeys.EXCHANGE + ".dlx")
                .deadLetterRoutingKey(ENRICHMENT_DLQ)
                .build();
    }
    // Dead Letter Queue

    @Bean
    public Queue enrichmentDlq() {
        return QueueBuilder.durable(ENRICHMENT_DLQ).build();
    }
    // Привязки

    /**
     * Привязка, только workout.finished и очередь enrichment.
     * В отличие от audit-service (binding "#" — все события),
     * enrichment-клиент подписан только на workout.finished.
     */
    @Bean
    public Binding enrichmentBinding(Queue enrichmentQueue, TopicExchange eventsExchange) {
        return BindingBuilder
                .bind(enrichmentQueue)
                .to(eventsExchange)
                .with(RoutingKeys.WORKOUT_FINISHED); // только workout.finished
    }

    @Bean
    public Binding enrichmentDlqBinding(Queue enrichmentDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(enrichmentDlq)
                .to(deadLetterExchange)
                .with(ENRICHMENT_DLQ);
    }



}
