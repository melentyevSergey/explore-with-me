package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto createEvents(Integer userId, NewEventDto eventDto);

    EventDto getEventByUserFullInfo(Integer userId, Integer eventId);

    List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size);

    EventDto getEventById(Integer eventId, HttpServletRequest request);

    List<Event> findEventsByIds(List<Integer> eventIds);

    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    List<ParticipationRequestDto> getRequestsByUser(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult changeStatusRequestsByUser(Integer userId, Integer eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    EventDto changeEventsByUser(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    EventDto changeEvents(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventDto> findEvents(List<Integer> users, List<String> states, List<Integer> categories,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

}
