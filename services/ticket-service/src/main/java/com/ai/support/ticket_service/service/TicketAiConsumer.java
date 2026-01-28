package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.dto.TicketCreatedEvent;
import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.domain.TicketPriority;
import com.ai.support.ticket_service.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketAiConsumer implements MessageListener {

    private final TicketRepository ticketRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());

            TicketCreatedEvent event =
                    objectMapper.readValue(json, TicketCreatedEvent.class);

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

            ticketRepository.save(ticket);

        } catch (Exception ex) {
            log.error("AI classification failed", ex);
        }
    }

}
