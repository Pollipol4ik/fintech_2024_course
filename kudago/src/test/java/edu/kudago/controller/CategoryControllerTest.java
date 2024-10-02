package edu.kudago.controller;

import com.redis.testcontainers.RedisContainer;
import edu.kudago.dto.Category;
import edu.kudago.exceptions.ResourceNotFoundException;
import edu.kudago.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    private Category category1;
    private Category category2;

    @BeforeEach
    public void setUp() {
        category1 = new Category(1, "art", "Art");
        category2 = new Category(2, "music", "Music");
    }


    @Test
    @DisplayName("Get all categories - success")
    public void testGetAllCategories() throws Exception {
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(category1, category2));

        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"},{\"id\":2,\"slug\":\"music\",\"name\":\"Music\"}]"));
    }


    @Test
    @DisplayName("Get category by ID - success")
    public void testGetCategoryById() throws Exception {
        Mockito.when(categoryService.getCategoryById(1)).thenReturn(category1);

        mockMvc.perform(get("/api/v1/places/categories/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"}"));
    }


    @Test
    @DisplayName("Get category by non-existent ID - failure")
    public void testGetCategoryByIdNotFound() throws Exception {
        Mockito.when(categoryService.getCategoryById(999)).thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/places/categories/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Category not found\"}"));
    }


    @Test
    @DisplayName("Create category - success")
    public void testCreateCategory() throws Exception {
        Mockito.when(categoryService.createCategory(any(Category.class))).thenReturn(category1);

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"art\",\"name\":\"Art\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"}"));
    }


    @Test
    @DisplayName("Update category - success")
    public void testUpdateCategory() throws Exception {
        Mockito.when(categoryService.updateCategory(eq(1), any(Category.class))).thenReturn(category1);

        mockMvc.perform(put("/api/v1/places/categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"art\",\"name\":\"Art\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"}"));
    }


    @Test
    @DisplayName("Update non-existent category - failure")
    public void testUpdateCategoryNotFound() throws Exception {
        Mockito.when(categoryService.updateCategory(eq(999), any(Category.class)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(put("/api/v1/places/categories/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"unknown\",\"name\":\"Unknown\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Category not found\"}"));
    }


    @Test
    @DisplayName("Delete category - success")
    public void testDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/places/categories/{id}", 1))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("Delete non-existent category - failure")
    public void testDeleteCategoryNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Category not found")).when(categoryService).deleteCategory(999);

        mockMvc.perform(delete("/api/v1/places/categories/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"success\":false,\"message\":\"Category not found\"}"));
    }
}
