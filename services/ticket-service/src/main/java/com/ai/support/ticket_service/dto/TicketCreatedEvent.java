package com.ai.support.ticket_service.dto;

import java.util.UUID;

public record TicketCreatedEvent(
        UUID ticketId,
        String title,
        String description
) {}