package com.example.demo.domain.input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "raw_events")
public class Event implements Serializable, Comparable<Event> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long primaryKey;

    private String id;
    private State state;
    private Type type;
    private String host;
    private long timestamp;

    @Override
    public int compareTo(Event event) {
        if (this.equals(event)) return 0;
        if (this.timestamp > event.timestamp) return 1;
        return -1;
    }
}
