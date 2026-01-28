package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.domain.TicketPriority;
import com.ai.support.ticket_service.dto.TicketCreatedEvent;
import com.ai.support.ticket_service.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AiClient aiClient;
    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    public Ticket create(String userId, String title, String description) {

        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .createdBy(userId)
                .build();

        ticket = ticketRepository.save(ticket);

        try {
            TicketCreatedEvent event =
                    new TicketCreatedEvent(ticket.getId(), title, description);

            String payload = objectMapper.writeValueAsString(event);

            redisTemplate.convertAndSend("ticket.created", payload);

        } catch (Exception e) {
            // fire-and-forget: do NOT break ticket creation
        }

        return ticket;
    }

    public List<Ticket> myTickets(String userId) {
        return ticketRepository.findByCreatedBy(userId);
    }
}
