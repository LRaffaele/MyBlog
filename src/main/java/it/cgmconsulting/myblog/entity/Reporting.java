package it.cgmconsulting.myblog.entity;

import it.cgmconsulting.myblog.entity.common.CreationUpdate;
import it.cgmconsulting.myblog.entity.common.ReportingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Reporting extends CreationUpdate {

    @EmbeddedId
    private ReportingId reportingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "reason", referencedColumnName = "reason", updatable = false),
            @JoinColumn(name = "start_date", referencedColumnName = "startDate", updatable = false)
    })
    private Reason reason;

    @Column(length = 25)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column (length = 25, nullable = false)
    private ReportingStatus status = ReportingStatus.OPEN;

    public Reporting(ReportingId reportingId, User user, Reason reason) {
        this.reportingId = reportingId;
        this.user = user;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporting reporting = (Reporting) o;
        return Objects.equals(reportingId, reporting.reportingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportingId);
    }
}
