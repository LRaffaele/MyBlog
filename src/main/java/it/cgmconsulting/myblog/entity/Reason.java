package it.cgmconsulting.myblog.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
@Check(constraints = "severity > 0")
public class Reason {

    @EmbeddedId
    private ReasonId reasonId;

    private LocalDate endDate;

    // indica la gravità della pena associata alla motivazione espressa in un int che rappresenta il numero giorni di Ban
    private int severity;

    public Reason(ReasonId reasonId, int severity) {
        this.reasonId = reasonId;
        this.severity = severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason reason = (Reason) o;
        return Objects.equals(reasonId, reason.reasonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonId);
    }
}
