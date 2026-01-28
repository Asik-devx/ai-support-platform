package com.ai.support.ticket_service.repository;

import com.ai.support.ticket_service.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByCreatedBy(String createdBy);
}
