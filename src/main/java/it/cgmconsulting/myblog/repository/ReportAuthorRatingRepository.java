package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.ReportAuthorRating;
import it.cgmconsulting.myblog.payload.response.ReportAuthorRatingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportAuthorRatingRepository extends JpaRepository<ReportAuthorRating, Long> {

    @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportAuthorRatingResponse(" +
            "p.author.id, " +
            "p.author.username, " +
            "COALESCE(AVG(r.rate),0.0), " +
            "(SELECT COUNT(a.id) FROM Post a WHERE (p.author.id = a.author.id) AND (p.publishedAt IS NOT NULL AND a.publishedAt BETWEEN :dateStart AND :dateEnd) GROUP BY a.author.id) " +
            ") FROM Post p " +
            "LEFT JOIN Rating r ON p.id = r.ratingId.post.id " +
            "WHERE r.updatedAt BETWEEN :dateStart AND :dateEnd " +
            "GROUP BY p.author.id, p.author.username")
    List<ReportAuthorRatingResponse> checkRatingAuthorWriterPost(@Param("dateStart")LocalDateTime dateStart, @Param("dateEnd")LocalDateTime dateEnd);


}
