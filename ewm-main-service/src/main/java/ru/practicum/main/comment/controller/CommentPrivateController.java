package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentByUserDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.CommentUpdateDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto create(@PathVariable(name = "userId") Integer userId,
                             @RequestParam(name = "eventId") Integer eventId,
                             @RequestBody @Valid NewCommentDto newComment) {
        log.debug("Создание комментария от текущего пользователя");
        return commentService.createComment(userId, eventId, newComment);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CommentByUserDto> getAllByUser(@PathVariable Integer userId,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.debug("Получение всех своих комментариев");
        return commentService.getAllByUser(userId, from, size);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteYourOwnComment(@PathVariable Integer userId,
                                     @PathVariable Integer commentId) {
        log.debug("Попытка удаления своего комментария");
        commentService.deleteYourOwnComment(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    public CommentUpdateDto update(@PathVariable Integer userId,
                                   @PathVariable Integer commentId,
                                   @RequestBody @Valid NewCommentDto updateComment) {
        log.debug("Попытка изменения комментария");
        return commentService.updateComment(updateComment, userId, commentId);
    }

}
