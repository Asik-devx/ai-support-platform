package com.ai.support.ticket_service.config;

import com.ai.support.ticket_service.service.TicketAiConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final RedisConnectionFactory factory;
    private final TicketAiConsumer consumer;
    private final ChannelTopic topic;

    @Bean
    RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(consumer, topic);
        return container;
    }
}
