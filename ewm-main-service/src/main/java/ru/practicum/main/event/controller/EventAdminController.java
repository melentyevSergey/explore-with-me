package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{eventId}")
    public EventDto changeEvents(@PathVariable(name = "eventId") @Positive Integer eventId,
                                     @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Редактирование данных события и его статуса (отклонение/публикация).");
        return eventService.changeEvents(eventId, updateEventAdminRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventDto> findEvents(@RequestParam(name = "users",      required = false)    List<Integer> users,
                                     @RequestParam(name = "states",     required = false)    List<String> states,
                                     @RequestParam(name = "categories", required = false)    List<Integer> categories,
                                     @RequestParam(name = "rangeStart", required = false)    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                     @RequestParam(name = "rangeEnd",   required = false)    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                     @RequestParam(name = "from",       defaultValue = "0")  @PositiveOrZero Integer from,
                                     @RequestParam(name = "size",       defaultValue = "10") @Positive Integer size) {
        log.debug("Попытка получения событий с возможностью фильтрации");
        return eventService.findEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
