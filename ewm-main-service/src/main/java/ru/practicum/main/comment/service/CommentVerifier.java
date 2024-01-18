package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventVerifier;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserVerifier;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentVerifier {
    private final CommentRepository repository;
    private final UserVerifier userVerifier;
    private final EventVerifier eventVerifier;

    Boolean checkAuthorship(Integer commentId, Integer authorId, String message) {
        if (Objects.equals(checkComment(commentId).getAuthor().getId(), checkUser(authorId).getId())) {
            return true;
        } else {
            throw new ConflictException(message);
        }
    }

    Comment checkComment(Integer commentId) {
        return repository.findById(commentId).orElseThrow(() ->
                new NotFoundException(String.format("Комментарий с идентификатором =%d не найден", commentId)));
    }

    User checkUser(Integer userId) {
        return userVerifier.checkUser(userId);
    }

    Event checkPublishedEvent(Integer eventId) {
        return eventVerifier.checkPublishedEvent(eventId);
    }


}
