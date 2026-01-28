package com.ai.support.ticket_service.dto;


public record CreateTicketRequest(
        String title,
        String description
) {}