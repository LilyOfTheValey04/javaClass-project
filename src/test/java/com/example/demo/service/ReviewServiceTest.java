package com.example.demo.service;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.review.ReviewCreateRequestDTO;
import com.exception.ResourceNotFound;
import com.mapper.ReviewMapper;
import com.model.Category;
import com.model.Material;
import com.model.Review;
import com.model.User;
import com.repository.CategoryRepository;
import com.repository.MaterialRepository;
import com.repository.ReviewRepository;
import com.service.CategoryService;
import com.service.MaterialService;
import com.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class ReviewServiceTest {
    private  ReviewRepository reviewRepository;
    private  ReviewMapper reviewMapper;
    private  MaterialService materialService;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewMapper = mock(ReviewMapper.class);
        materialService = mock(MaterialService.class);
        reviewService = new ReviewService(
                reviewRepository, reviewMapper, materialService);
    }
    @Test
    void getAllReviews_whenFound() {
        Review existing1 = new Review();
        existing1.setId(1L);
        existing1.setText("Good");
        existing1.setRating(5);

        Review existing2 = new Review();
        existing2.setId(1L);
        existing2.setText("Bad");
        existing2.setRating(1);

        when(reviewRepository.findAll()).thenReturn(List.of(existing1, existing2));

        List<Review> result  = reviewService.getAllReviews();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(existing1, existing2);

        verify(reviewRepository).findAll();
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getAllReviews_whenNotFound(){
        when(reviewRepository.findAll()).thenReturn(List.of());

        assertThat(reviewService.getAllReviews()).isEmpty();

        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void deleteReview_whenFound() {
        when(reviewRepository.existsById(1L))
                .thenReturn(true);

        reviewService.deleteReview(1L);

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_whenNotFound_shouldThrow() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFound.class,
                () -> reviewService.deleteReview(1L));

        verify(reviewRepository).existsById(1L);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void createReview_returnsReview(){
        ReviewCreateRequestDTO reviewCreateRequestDTO = new ReviewCreateRequestDTO(
                "Good",
                10,
                1L);

        Material material = new Material();
        material.setId(1L);
        Review review = new Review();

        when(reviewMapper.toReview(reviewCreateRequestDTO)).thenReturn(review);
        when(materialService.getMaterialById(1L)).thenReturn(material);
        when(reviewRepository.save(review)).thenReturn(review);

        Review result = reviewService.createReview(reviewCreateRequestDTO);

        assertNotNull(result);
        assertEquals(material, result.getMaterial());
        verify(reviewRepository).save(review);
    }

    @Test
    void createReview_returnsNotFound(){
        ReviewCreateRequestDTO dto = new ReviewCreateRequestDTO(
                "Good",
                10,
                1L
        );

        when(materialService.getMaterialById(1L))
                .thenThrow(new ResourceNotFound(Material.class, 1L));

        // when + then
        assertThrows(ResourceNotFound.class,
                () -> reviewService.createReview(dto));

        verify(materialService).getMaterialById(1L);
        verify(reviewRepository, never()).save(any());

    }
}
