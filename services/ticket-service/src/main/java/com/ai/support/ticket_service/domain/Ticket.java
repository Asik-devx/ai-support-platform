package com.ai.support.ticket_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Column(length = 4000)
    private String description;

    private String createdBy; // userId from JWT

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private Instant createdAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketMessage> messages = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.status = TicketStatus.OPEN;
    }
}
