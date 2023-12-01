package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.entity.Post;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.entity.common.ImagePosition;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.payload.request.PostRequest;
import it.cgmconsulting.myblog.payload.response.BestRatedPost;
import it.cgmconsulting.myblog.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.payload.response.PostSearchResponse;
import it.cgmconsulting.myblog.repository.CategoryRepository;
import it.cgmconsulting.myblog.repository.PostRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> createPost (PostRequest request, UserPrincipal principal){
        if(postRepository.existsByTitle(request.getTitle()))
            return new ResponseEntity("A post with this title is already present", HttpStatus.BAD_REQUEST);
        Post post = new Post(request.getTitle(), request.getOverview(), request.getContent(), new User(principal.getId()));
        postRepository.save(post);
        return new ResponseEntity("New post successfully created", HttpStatus.CREATED); // codice 201, per differenziarlo dall'ok (codice 200)
    }

    @Transactional
    public ResponseEntity<?> updatePost(long id, PostRequest request, UserPrincipal principal){

        if(postRepository.existsByTitleAndIdNot(request.getTitle(), id))
            return new ResponseEntity("Title already present in another post", HttpStatus.BAD_REQUEST);

        Post post = findPost(id);

        post.setTitle(request.getTitle());
        post.setOverview(request.getOverview());
        post.setContent(request.getContent());
        post.setAuthor(new User(principal.getId()));
        post.setPublishedAt(null);

        return new ResponseEntity("Post has been updated", HttpStatus.OK);
    }
    @Transactional
   public ResponseEntity<?> publishPost(long postId, LocalDateTime publishedAt){

        if(publishedAt.isBefore(LocalDateTime.now()))
            return new ResponseEntity("Selected publication date is in the past", HttpStatus.BAD_REQUEST);

        Post post = findPost(postId);
        String msg = "Post published";

        if(publishedAt == null) {
            post.setPublishedAt(LocalDateTime.now());
        } else {
            post.setPublishedAt(publishedAt);
            msg = "Post will be published in the future";
        }
        return new ResponseEntity(msg, HttpStatus.OK);
   }

   protected Post findPost(long postId){
       Post post = postRepository.findById(postId).orElseThrow(
               () -> new ResourceNotFoundException("Post", "id", postId)
       );
       return post;
   }

    protected Post findVisiblePost(long postId, LocalDateTime now){
        Post post = postRepository.findByIdAndPublishedAtNotNullAndPublishedAtBefore(postId, now).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );
        return post;
    }

   @Transactional
    public ResponseEntity<?> addCategories(long postId, Set<String> categories) {
        Post post = findPost(postId);
        Set<Category> categoriesToAdd = categoryRepository.getCategoriesIn(categories);
        if(categoriesToAdd.isEmpty())
            return new ResponseEntity<>("No categories found", HttpStatus.NOT_FOUND);
        post.setCategories(categoriesToAdd);
        return new ResponseEntity<>("Categories added to post", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> removeCategories(long postId) {
        Post post = findPost(postId);
        post.getCategories().clear();
        return new ResponseEntity<>("All categories successfully removed", HttpStatus.OK);
    }

    public ResponseEntity<?> getPostBoxes(int pageNumber, int pageSize, String direction, String sortBy, String imagePosition){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostBoxesResponse> result = postRepository.getPostBoxes(pageable, LocalDateTime.now(), ImagePosition.valueOf(imagePosition.toUpperCase()));
        List<PostBoxesResponse> list = new ArrayList<>();
        if (result.hasContent()) {
            list = result.getContent();
            for (PostBoxesResponse pbr : list){
                pbr.setCategories(postRepository.getCategoriesByPost(pbr.getId()));
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
     }

     public ResponseEntity<?> getPostDetail(long postId, String position){
        return new ResponseEntity<>(postRepository.getPostDetail(postId, LocalDateTime.now(), ImagePosition.valueOf(position.toUpperCase())), HttpStatus.OK);
     }

    public ResponseEntity<?> getPostsByCategory(String categoryName){
        return new ResponseEntity(postRepository.getPostsByCategory(categoryName, LocalDateTime.now(), ImagePosition.PRE), HttpStatus.OK);
    }
    public ResponseEntity<?> getPostByKeyword(String keyword, boolean isCaseSensitive, boolean isExactMatch, ImagePosition position){
        List<PostSearchResponse> listaCompleta = postRepository.getPublishedPosts("%"+keyword+"%", LocalDateTime.now(), position);


        List<PostSearchResponse> nuovaLista = new ArrayList<>();
        Pattern pattern = null;

        if(!isCaseSensitive && !isExactMatch) {
             pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        } else if (!isCaseSensitive && isExactMatch){
             pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
        } else if (isCaseSensitive && !isExactMatch){
             pattern = Pattern.compile(keyword);
        } else if (isCaseSensitive && isExactMatch){
             pattern = Pattern.compile("\\b" + keyword + "\\b");
        }

            Pattern finalPattern = pattern;
            nuovaLista = listaCompleta.stream()
                    .filter(post -> finalPattern.matcher(post.getTitle().concat(" ").concat(post.getOverview().concat(" ").concat(post.getContent()))).find())
                    .collect(Collectors.toList());

        return new ResponseEntity(nuovaLista, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostsByAuthor(String author) {
        return new ResponseEntity(postRepository.getPostsByAuthor(author, LocalDateTime.now(), ImagePosition.PRE), HttpStatus.OK);
    }


    public ResponseEntity<?> getMostRatedInPeriod(LocalDate start, LocalDate end) {
        LocalDateTime s = null;
        LocalDateTime e = null;


        if(start == null && end == null) {
            LocalDateTime initial = LocalDateTime.now();
            s = initial.withDayOfMonth(1);
            e = initial.withDayOfMonth(initial.toLocalDate().getDayOfMonth());
        } else {
            s = start.atTime(0,0,0);
            e = end.atTime(23,59,59);
        }

        List<BestRatedPost> bestRatedPosts = postRepository.getMostRatedInPeriod(s,e);
        return new ResponseEntity(bestRatedPosts, HttpStatus.OK);
    }


}