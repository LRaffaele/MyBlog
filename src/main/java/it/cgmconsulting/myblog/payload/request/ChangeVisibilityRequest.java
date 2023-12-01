package it.cgmconsulting.myblog.payload.request;

import it.cgmconsulting.myblog.entity.Category;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class ChangeVisibilityRequest {

    @NotEmpty
    private List<Category> categories;
}
