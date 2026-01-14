package com.controller;

import com.dto.review.ReviewCreateRequestDTO;
import com.dto.review.ReviewCreateResponseDTO;
import com.mapper.ReviewMapper;
import com.model.Review;
import com.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping
    public List<ReviewCreateResponseDTO> getAllReviews() {
        return reviewService.getAllReviews()
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewCreateResponseDTO> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return new ResponseEntity<>(reviewMapper.toResponse(review), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewCreateResponseDTO> createReview(
            @Valid @RequestBody ReviewCreateRequestDTO reviewCreateRequestDTO) {
        Review review = reviewService.createReview(reviewCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewMapper.toResponse(review));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable @Positive Long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
