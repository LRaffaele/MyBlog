package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.ReportAuthorRating;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.payload.response.ReportAuthorRatingResponse;
import it.cgmconsulting.myblog.repository.ReportAuthorRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor @Slf4j
public class ScheduledService {

    private final ReportAuthorRatingRepository reportAuthorRatingRepository;
    @Scheduled(cron = "0 0 1 1 * *" )
    public void generateReportAuthorRating(){
        System.out.println(" #### SCHEDULAZIONE MENSILE PER AUTHOR RATING #### ");

        LocalDateTime dataOdierna = LocalDateTime.now();
        LocalDateTime primoGiorno = dataOdierna.minusMonths(1);
        LocalDateTime ultimoGiorno = dataOdierna.minusDays(1);

        List<ReportAuthorRatingResponse> listAuthorRating = reportAuthorRatingRepository.checkRatingAuthorWriterPost(primoGiorno, ultimoGiorno);
        List<ReportAuthorRating> rar = new ArrayList<>();
        for(ReportAuthorRatingResponse r : listAuthorRating){
            rar.add(new ReportAuthorRating(new User(r.getId()), r.getAverage(), r.getWrittenPosts().byteValue(), LocalDate.now()));
            log.info(r.toString());
        }
        reportAuthorRatingRepository.saveAll(rar);

    }
}
