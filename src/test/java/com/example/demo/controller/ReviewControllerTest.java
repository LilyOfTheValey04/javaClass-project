package com.example.demo.controller;

import com.controller.MaterialController;
import com.controller.ReviewController;
import com.dto.review.ReviewCreateRequestDTO;
import com.dto.review.ReviewCreateResponseDTO;
import com.exception.ResourceNotFound;
import com.mapper.MaterialMapper;
import com.mapper.ReviewMapper;
import com.model.Review;
import com.service.CategoryService;
import com.service.MaterialService;
import com.service.ReviewService;
import com.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapper reviewMapper;

    @Test
    void getAllReviews_returnsList() throws Exception {
        Review review = new Review();
        ReviewCreateResponseDTO dto = new ReviewCreateResponseDTO(
                 "Good", 5
        );

        when(reviewService.getAllReviews()).thenReturn(List.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(dto);

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].text").value("Good"));
    }

    @Test
    void getReviewById_whenFound_returnsReview() throws Exception {
        Review review = new Review();
        ReviewCreateResponseDTO dto = new ReviewCreateResponseDTO("Good", 5);

        when(reviewService.getReviewById(1L)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(dto);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Good"));
    }

    @Test
    void getReviewById_whenNotFound_returns404() throws Exception {
        when(reviewService.getReviewById(1L))
                .thenThrow(new ResourceNotFound(Review.class, 1L));

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReview_returnsCreated() throws Exception {
        ReviewCreateRequestDTO request = new ReviewCreateRequestDTO(
                "Good", 5, 1L
        );

        Review review = new Review();
        ReviewCreateResponseDTO response = new ReviewCreateResponseDTO(
                 "Good", 5
        );

        when(reviewService.createReview(any())).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "text": "Good",
                          "rating": 5,
                          "materialId": 1
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Good"));
    }

    @Test
    void createReview_whenRatingNegative_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "text": "ddsadsad",
                          "rating": -1,
                          "materialId": 1
                        }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_whenRatingBiggerThan10_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "text": "ddsadsad",
                          "rating": 11,
                          "materialId": 1
                        }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_whenTextIsNull_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "text": null,
                          "rating": 10,
                          "materialId": 1
                        }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview_returnsNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/api/reviews/delete/1"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(1L);
    }

    @Test
    void deleteReview_whenIdIsNegative_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/reviews/delete/-1"))
                .andExpect(status().isBadRequest());
    }

}
