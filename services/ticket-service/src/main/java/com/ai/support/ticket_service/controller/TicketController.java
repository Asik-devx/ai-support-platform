package com.ai.support.ticket_service.controller;

import com.ai.support.ticket_service.domain.Ticket;
import com.ai.support.ticket_service.dto.CreateTicketRequest;
import com.ai.support.ticket_service.repository.TicketRepository;
import com.ai.support.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @PostMapping
    public Ticket create(
            @RequestBody CreateTicketRequest request,
            Authentication auth
    ) {
        return ticketService.create(
                auth.getPrincipal().toString(),
                request.title(),
                request.description()
        );
    }

    @GetMapping
    public List<Ticket> myTickets(Authentication auth) {
        return ticketService.myTickets(auth.getPrincipal().toString());
    }
    @GetMapping("/{id}")
    public Ticket get(@PathVariable UUID id) {
        return ticketRepository.findById(id).orElseThrow();
    }
}
