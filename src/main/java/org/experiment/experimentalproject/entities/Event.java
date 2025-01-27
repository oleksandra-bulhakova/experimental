package org.experiment.experimentalproject.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "event")
@ToString(exclude = {"author"})
@EqualsAndHashCode(exclude = {"author"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private String description;
}
