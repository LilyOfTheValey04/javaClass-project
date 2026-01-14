package com.service;

import com.exception.ResourceNotFound;
import com.dto.review.ReviewCreateRequestDTO;
import com.mapper.ReviewMapper;
import com.model.Material;
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
    private final  MaterialService materialService;

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Review.class, id));
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review createReview(ReviewCreateRequestDTO reviewCreateRequestDTO) {
        Material material = materialService.getMaterialById(reviewCreateRequestDTO.materialId());

        Review review = reviewMapper.toReview(reviewCreateRequestDTO);
        review.setMaterial(material);
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id))
            reviewRepository.deleteById(id);
        else
            throw new ResourceNotFound(Review.class, id);

    }
}
