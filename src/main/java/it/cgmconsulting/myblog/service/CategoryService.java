package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Category;
import it.cgmconsulting.myblog.payload.request.ChangeVisibilityRequest;
import it.cgmconsulting.myblog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResponseEntity<?> addCategory(String categoryName){
        if(categoryRepository.existsByCategoryName(categoryName))
            return new ResponseEntity("Category already present", HttpStatus.BAD_REQUEST);
        categoryRepository.save(new Category(categoryName));
        return new ResponseEntity("Category successfully created", HttpStatus.OK);
    }

    public ResponseEntity<?> getVisibileCategories() {
        return new ResponseEntity(categoryRepository.getVisibleCategories(), HttpStatus.OK);
    }

    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity(categoryRepository.getAllCategories(), HttpStatus.OK);
    }

    public ResponseEntity<String> changeVisibility(ChangeVisibilityRequest request){
        categoryRepository.saveAll(request.getCategories());
        return new ResponseEntity<String>("Category visibility has been updated", HttpStatus.OK);
    }

}

