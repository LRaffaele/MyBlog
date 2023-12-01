package it.cgmconsulting.myblog.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @AllArgsConstructor @EqualsAndHashCode @NoArgsConstructor @ToString
public class AvatarId implements Serializable {

    @OneToOne
    @JoinColumn(name="id", nullable = false)
    private User user;
}
