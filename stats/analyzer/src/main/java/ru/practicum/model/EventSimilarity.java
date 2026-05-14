package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_similarity", schema = "analyzer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_a")
    private Long eventA;

    @Column(name = "event_b")
    private Long eventB;

    @Column(name = "score")
    private Double score;
}
