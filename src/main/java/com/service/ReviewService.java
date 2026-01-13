package com.service;

import com.exception.ResourceNotFound;
import com.dto.review.ReviewCreateRequestDTO;
import com.mapper.ReviewMapper;
import com.model.Review;
import com.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private  final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Review.class, id));
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review createReview(ReviewCreateRequestDTO reviewCreateRequestDTO) {
        Review review = reviewMapper.toReview(reviewCreateRequestDTO);

        return reviewRepository.save(review);
    }
}
