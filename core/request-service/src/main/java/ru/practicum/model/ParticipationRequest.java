package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.util.ParticipationRequestStatus;

import java.sql.Timestamp;

@Entity
@Table (name = "participation_requests", schema = "request")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp created;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;
}
