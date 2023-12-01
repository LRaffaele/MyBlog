package it.cgmconsulting.myblog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ReportAuthorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="author", nullable = false)
    private User author;

    double average;

    private byte postWritten;

    private LocalDate actually;

    public ReportAuthorRating(User author, double average, byte postWritten, LocalDate actually) {
        this.author = author;
        this.average = average;
        this.postWritten = postWritten;
        this.actually = actually;
    }
}
