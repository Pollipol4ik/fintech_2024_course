package edu.kudago.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@ToString
@EqualsAndHashCode(of = "name")
@Getter
@Setter
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_pk_seq")
    @GenericGenerator(name = "events_pk_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "events_pk_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            })
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Place cannot be null")
    @JoinColumn(name = "place_id", nullable = false)
    private LocationEntity place;
}
