package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventDto createEvents(@PathVariable(name = "userId") @Positive Integer userId,
                                 @RequestBody @Valid NewEventDto eventDto) {
        log.debug("Попытка добавления нового события.");
        return eventService.createEvents(userId, eventDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{eventId}")
    public EventDto getEventByUserFullInfo(@PathVariable(name = "userId") Integer userId,
                                           @PathVariable(name = "eventId") Integer eventId) {
        log.debug("Получение полной информации о событии добавленном текущим пользователем");
        return eventService.getEventByUserFullInfo(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable(name = "userId") Integer userId,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.debug("Получение событий, добавленных текущим пользователем");
        return eventService.getEventsByUser(userId, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{eventId}")
    public EventDto changeEventsByUser(@PathVariable(name = "userId") Integer userId,
                                       @PathVariable(name = "eventId") Integer eventId,
                                       @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Изменение события добавленного текущим пользователем");
        return eventService.changeEventsByUser(userId, eventId, updateEventUserRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable(name = "userId") Integer userId,
                                                           @PathVariable(name = "eventId") Integer eventId) {
        log.debug("Получение информации о запросах на участие в событии текущего пользователя");
        return eventService.getRequestsByUser(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResult changeStatusRequestsByUser(@PathVariable(name = "userId") Integer userId,
                                                                     @PathVariable(name = "eventId") Integer eventId,
                                                                     @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");
        return eventService.changeStatusRequestsByUser(userId, eventId, eventRequestStatusUpdateRequest);
    }

}
