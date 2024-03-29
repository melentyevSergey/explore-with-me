package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.comment.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByEvent_Id(Integer eventId, Pageable page);

    List<Comment> findAllByAuthor_Id(Integer userId, Pageable page);
}
