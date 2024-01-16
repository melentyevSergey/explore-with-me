package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.stat.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {
    private final EventService eventService;
    private final StatsClient client;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{eventId}")
    public EventDto getEventById(@PathVariable(name = "eventId") @Positive Integer eventId,
                                                                           HttpServletRequest request) {
        log.debug("Получение подробной информации об опубликованном событии по его идентификаторе");
        client.hit(request);
        return eventService.getEventById(eventId, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text",          required = false)       String text,
                                         @RequestParam(name = "categories",    required = false)       List<Integer> categories,
                                         @RequestParam(name = "paid",          required = false)       Boolean paid,
                                         @RequestParam(name = "rangeStart",    required = false)       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd",      required = false)       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "onlyAvailable", required = false)       Boolean onlyAvailable,
                                         @RequestParam(name = "sort",          defaultValue = "views") String sort,
                                         @RequestParam(name = "from",          defaultValue = "0")     @PositiveOrZero Integer from,
                                         @RequestParam(name = "size",          defaultValue = "10")    @Positive Integer size,
                                                                                                 HttpServletRequest request) {
        log.debug("Попытка получения событий с возможностью фильтрации");
        client.hit(request);
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }
}
