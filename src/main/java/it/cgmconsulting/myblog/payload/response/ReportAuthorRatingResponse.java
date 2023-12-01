package it.cgmconsulting.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReportAuthorRatingResponse {

    private long id;
    private String username;
    private double average;
    private Long writtenPosts;

}
