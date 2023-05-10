package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.storage.likeReview.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final LikeReviewStorage likeReviewStorage;
    private final EventService eventService;

    public Review create(Review review) {
        Review reviewCreated = reviewStorage.create(review);
        eventService.createEvent(reviewCreated.getUserId(), EventType.REVIEW, EventOperation.ADD, reviewCreated.getReviewId());
        return reviewCreated;
    }

    public Review update(Review review) {
        Review reviewUpdated = reviewStorage.update(review).orElseThrow(() -> new NotFoundException("Отзыв не найден."));
        eventService.createEvent(reviewUpdated.getUserId(), EventType.REVIEW, EventOperation.UPDATE, reviewUpdated.getReviewId());
        return reviewUpdated;
    }

    public void delete(Integer id) {
        Review review = findById(id);
        reviewStorage.delete(id);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
    }

    public Review findById(Integer id) {
        return reviewStorage.findById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден."));
    }

    public List<Review> findAll(Integer filmId, Integer count) {
        return reviewStorage.findAll(filmId, count);
    }

    public void createLike(Integer id, Integer userId) {
        likeReviewStorage.createLike(id, userId);
    }

    public void createDislike(Integer id, Integer userId) {
        likeReviewStorage.createDislike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        likeReviewStorage.deleteLike(id, userId);
    }

    public void deleteDislike(Integer id, Integer userId) {
        likeReviewStorage.deleteDislike(id, userId);
    }
}