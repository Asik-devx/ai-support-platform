package com.ai.support.ticket_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    // 🔹 Used for publishing events (simple & fast)
    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory factory
    ) {
        return new StringRedisTemplate(factory);
    }

    // 🔹 Optional: structured Redis access (future use)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    // 🔹 Pub/Sub topic
    @Bean
    public ChannelTopic ticketCreatedTopic() {
        return new ChannelTopic("ticket.created");
    }
}