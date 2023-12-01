package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final PostService postService;
    private final RatingRepository ratingRepository;
    public ResponseEntity<?> addRating(long userId, long postId, byte rate) {
        Post post = postService.findVisiblePost(postId, LocalDateTime.now());
        Rating rating = new Rating(new RatingId(new User(userId), post), rate);
        ratingRepository.save(rating);
        return new ResponseEntity<>("Your vote has been successfully registered", HttpStatus.OK);
    }
}
