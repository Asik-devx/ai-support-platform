package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.domain.TicketAiStatus;
import com.ai.support.ticket_service.dto.TicketCreatedEvent;
import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.domain.TicketPriority;
import com.ai.support.ticket_service.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketAiConsumer implements MessageListener {

    private final TicketRepository ticketRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_RETRIES = 3;
    private static final String DLQ_KEY = "ticket.ai.dlq";
    private final StringRedisTemplate redisTemplate;
    private static final String IDEMPOTENCY_KEY_PREFIX = "ticket:ai:processed:";
    private static final int IDEMPOTENCY_TTL_SECONDS = 86400; // 24 hours




    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());

            TicketCreatedEvent event =
                    objectMapper.readValue(json, TicketCreatedEvent.class);

            String idempotencyKey =
                    IDEMPOTENCY_KEY_PREFIX + event.ticketId();

            Boolean firstTime =
                    redisTemplate.opsForValue()
                            .setIfAbsent(idempotencyKey, "true", IDEMPOTENCY_TTL_SECONDS, TimeUnit.SECONDS);

            if (Boolean.FALSE.equals(firstTime)) {
                log.info(
                        "Skipping duplicate AI processing for ticket {}",
                        event.ticketId()
                );
                return;
            }

            var aiResponse = aiClient.classify(
                    event.title(),
                    event.description()
            );

            Ticket ticket = ticketRepository
                    .findById(event.ticketId())
                    .orElseThrow();

            ticket.setPriority(
                    TicketPriority.valueOf(aiResponse.priority())
            );
            ticket.setAiStatus(TicketAiStatus.COMPLETED);
            ticketRepository.save(ticket);

            log.info("AI classification completed for ticket {}", event.ticketId());

        } catch (Exception ex) {
            handleFailure(message, ex);
        }
    }

    private void handleFailure(Message message, Exception ex) {
        try {
            String json = new String(message.getBody());
            TicketCreatedEvent event =
                    objectMapper.readValue(json, TicketCreatedEvent.class);

            int nextRetry = event.retryCount() + 1;

            if (nextRetry <= MAX_RETRIES) {
                log.warn(
                        "AI failed for ticket {}. Retrying {}/{}",
                        event.ticketId(),
                        nextRetry,
                        MAX_RETRIES
                );

                TicketCreatedEvent retryEvent =
                        new TicketCreatedEvent(
                                event.ticketId(),
                                event.title(),
                                event.description(),
                                nextRetry
                        );

                String payload =
                        objectMapper.writeValueAsString(retryEvent);

                // re-publish (simple retry)
                redisTemplate.convertAndSend("ticket.created", payload);

            } else {
                log.error(
                        "AI failed permanently for ticket {}. Sending to DLQ",
                        event.ticketId(),
                        ex
                );

                redisTemplate.opsForList()
                        .rightPush(DLQ_KEY, json);

                ticketRepository.findById(event.ticketId()).ifPresent(ticket -> {
                    ticket.setAiStatus(TicketAiStatus.FAILED);
                    ticketRepository.save(ticket);
                });
            }

        } catch (Exception fatal) {
            log.error("Retry handling failed", fatal);
        }
    }
}
