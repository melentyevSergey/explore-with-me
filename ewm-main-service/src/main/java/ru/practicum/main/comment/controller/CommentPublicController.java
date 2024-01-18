package ru.practicum.main.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPublicController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CommentDto> getComments(@RequestParam(name = "eventId") Integer eventId,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.debug("Получениe комментариев по событию: {}", eventId);
        return commentService.getComments(eventId, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable Integer commentId) {
        log.debug("Получение комментария по идентификатору");
        return commentService.getById(commentId);
    }
}
