package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.StudentCategoryEntity;
import com.bosams.repository.StudentCategoryRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-categories")
public class StudentCategoriesController {
    private final StudentCategoryRepository categories;

    public StudentCategoriesController(StudentCategoryRepository categories) {
        this.categories = categories;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentCategoryEntity> list(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return activeOnly
                ? categories.findByStatusOrderByNameAsc(Enums.EntityStatus.ACTIVE)
                : categories.findAll().stream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentCategoryEntity create(@Valid @RequestBody CategoryRequest request) {
        String normalizedName = request.name().trim();
        categories.findByNameIgnoreCase(normalizedName).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "CATEGORY_EXISTS", "Category already exists");
        });
        StudentCategoryEntity category = new StudentCategoryEntity();
        category.setName(normalizedName);
        category.setStatus(Enums.EntityStatus.ACTIVE);
        return categories.save(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public StudentCategoryEntity update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        StudentCategoryEntity category = categories.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found"));
        String normalizedName = request.name().trim();
        categories.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ApiException(HttpStatus.CONFLICT, "CATEGORY_EXISTS", "Category already exists");
                });
        category.setName(normalizedName);
        return categories.save(category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL')")
    public void delete(@PathVariable Long id) {
        StudentCategoryEntity category = categories.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found"));
        categories.delete(category);
    }

    public record CategoryRequest(@NotBlank String name) {}
}
