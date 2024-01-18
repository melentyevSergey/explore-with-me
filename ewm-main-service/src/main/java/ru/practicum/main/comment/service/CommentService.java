package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.CommentByUserDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentUpdateDto;
import ru.practicum.main.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getComments(Integer eventId, Integer from, Integer size);

    CommentDto createComment(Integer userId, Integer eventId, NewCommentDto newCommentDto);

    List<CommentByUserDto> getAllByUser(Integer userId, Integer from, Integer size);

    CommentDto getById(Integer commentId);

    void deleteYourOwnComment(Integer commentId, Integer userId);

    void deleteComment(Integer commentId);

    CommentUpdateDto updateComment(NewCommentDto updateComment, Integer userId, Integer commentId);
}
