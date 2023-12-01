package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Reason;
import it.cgmconsulting.myblog.entity.ReasonId;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.repository.ReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReasonService {

    private final ReasonRepository reasonRepository;

    public ResponseEntity<?> createReason (ReasonRequest request){
        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());
        if (reasonRepository.existsById(reasonId))
            return new ResponseEntity<>("Reason already present", HttpStatus.BAD_REQUEST);
        Reason reason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity<>(reasonRepository.save(reason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> updateReason(ReasonRequest request) {

        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());

        Optional<Reason> reasonVecchia = reasonRepository.findByReasonIdReasonAndEndDateIsNull(request.getReason());

        if (reasonVecchia.isEmpty())
            return new ResponseEntity<>("Reason not found, please create a new reason before updating ", HttpStatus.NOT_FOUND);

        if (reasonVecchia.get().getSeverity() == request.getSeverity())
            return new ResponseEntity<>("The selected severity is the same as the previous one", HttpStatus.BAD_REQUEST);

        reasonVecchia.get().setEndDate(request.getStartDate().minusDays(1));

        Reason nuovaReason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity<>(reasonRepository.save(nuovaReason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> removeReason(String reason, LocalDate endDate) {
        Optional<Reason> reasonVecchia = reasonRepository.findByReasonIdReasonAndEndDateIsNull(reason);

        if (reasonVecchia.isEmpty())
            return new ResponseEntity<>("Reason not found, please create a new reason before updating ", HttpStatus.NOT_FOUND);

        reasonVecchia.get().setEndDate(endDate);
        return new ResponseEntity<>("Reason " + reason + " has been set to expire on " + endDate, HttpStatus.OK);
    }

    public ResponseEntity<?> getReasons() {
        List<String> elencoReason = reasonRepository.getReasons(LocalDate.now());
        return new ResponseEntity<>(elencoReason, HttpStatus.OK);
    }

    protected Reason getValidReason (String reason){
        Reason r = reasonRepository.getValidReason(LocalDate.now(), reason).orElseThrow(
                () -> new ResourceNotFoundException("Reason", "reason", reason)
        );
        return r;
    }
}
