package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.CommentByUserDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentUpdateDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.utility.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentVerifier verifier;
    private final CommentMapper mapper;

    @Override
    public List<CommentDto> getComments(Integer eventId, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);

        return commentRepository.findCommentsByEvent_Id(verifier.checkPublishedEvent(eventId).getId(), page).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Integer userId, Integer eventId, NewCommentDto newCommentDto) {
        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .event(verifier.checkPublishedEvent(eventId))
                .author(verifier.checkUser(userId))
                .created(LocalDateTime.now())
                .build();
        return mapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentByUserDto> getAllByUser(Integer userId, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);

        return commentRepository.findAllByAuthor_Id(userId, page).stream()
                .map(mapper::toByUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getById(Integer commentId) {
        return mapper.toDto(verifier.checkComment(commentId));
    }

    @Override
    @Transactional
    public void deleteYourOwnComment(Integer commentId, Integer userId) {
        if (verifier.checkAuthorship(commentId, userId, "Вы не можете удалять чужие комментарии")) {
            commentRepository.deleteById(commentId);
        }
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(verifier.checkComment(commentId).getId());
    }

    @Override
    @Transactional
    public CommentUpdateDto updateComment(NewCommentDto updateComment, Integer userId, Integer commentId) {
        Comment comment = verifier.checkComment(commentId);
        verifier.checkAuthorship(commentId, userId, "Вы не можете изменять чужие комментарии");
            CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                    .id(comment.getId())
                    .oldText(comment.getText())
                    .newText(updateComment.getText())
                    .build();
            comment.setText(updateComment.getText());
            commentRepository.save(comment);
            return commentUpdateDto;
    }
}
