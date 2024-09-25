package edu.kudago.controller;

import edu.kudago.aspect.LogExecutionTime;
import edu.kudago.dto.Category;
import edu.kudago.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/places/categories")
@RequiredArgsConstructor
@LogExecutionTime
public class CategoryController {

    private CategoryService categoryService;


    @Operation(summary = "Get all categories", description = "�������� ������ ���� ��������� ����")
    @GetMapping
    public Iterable<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Get category by ID", description = "�������� ��������� �� � �������������� (ID)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "��������� �������"),
            @ApiResponse(responseCode = "404", description = "��������� �� �������")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create category", description = "������� ����� ���������")
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @Operation(summary = "Update category by ID", description = "�������� ��������� �� � �������������� (ID)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "��������� ���������"),
            @ApiResponse(responseCode = "404", description = "��������� �� �������")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @Operation(summary = "Delete category by ID", description = "������� ��������� �� � �������������� (ID)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "��������� �������"),
            @ApiResponse(responseCode = "404", description = "��������� �� �������")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
