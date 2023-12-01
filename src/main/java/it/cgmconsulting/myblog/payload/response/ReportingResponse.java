package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter@Setter @NoArgsConstructor @AllArgsConstructor
public class ReportingResponse {

    private long commentId;
    private String comment;
    private String reason;
    private String note;
    private String username; // segnalante
    private LocalDateTime updatedAt;


}
