package com.ai.support.ticket_service.service;

import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket create(String userId, String title, String description) {
        Ticket ticket = Ticket.builder()
                .title(title)
                .description(description)
                .createdBy(userId)
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> myTickets(String userId) {
        return ticketRepository.findByCreatedBy(userId);
    }
}