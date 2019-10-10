package com.example.demo.domain.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;

@Entity
@Table(name = "processed_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "duration", nullable = false)
    private Duration eventDuration;

    @Column(name = "host")
    private String host;

    @Column(name = "alert")
    private boolean alert;

}
