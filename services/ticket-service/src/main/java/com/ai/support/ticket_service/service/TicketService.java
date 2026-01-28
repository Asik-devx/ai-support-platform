package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.domain.TicketPriority;
import com.ai.support.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AiClient aiClient;

    public Ticket create(String userId, String title, String description) {

        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .createdBy(userId)
                .build();

        ticket = ticketRepository.save(ticket);

        // AI classification
        try {
            var aiResponse = aiClient.classify(title, description);
            ticket.setPriority(
                    TicketPriority.valueOf(aiResponse.priority())
            );
            ticketRepository.save(ticket);
        } catch (Exception ex) {
            // graceful degradation
        }

        return ticket;
    }

    public List<Ticket> myTickets(String userId) {
        return ticketRepository.findByCreatedBy(userId);
    }
}
