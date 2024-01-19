package ru.practicum.main.event.service;

import com.google.gson.Gson;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.model.StateActionAdmin;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidTimeException;
import ru.practicum.main.location.model.Location;
import ru.practicum.main.location.service.LocationService;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.mapper.ParticipationRequestMapper;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.requests.model.ParticipationRequestStatus;
import ru.practicum.main.requests.repository.ParticipationRequestRepository;
import ru.practicum.main.stat.client.StatsClient;
import ru.practicum.main.stat.dto.ViewStats;
import ru.practicum.main.utility.Filter;
import ru.practicum.main.utility.Page;
import ru.practicum.main.utility.QPredicates;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventVerifier verifier;
    private final LocationService locationService;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;
    private final CategoryMapper categoryMapper;
    private final StatsClient client;
    private final Gson gson = new Gson();

    @Override
    @Transactional
    public EventDto createEvents(Integer userId, NewEventDto newEventDto) {
        newEventDto.setLocation(locationService.save(newEventDto.getLocation()));

        log.debug("Попытка добавления нового события.");

        Event event = eventMapper.toEntity(
                newEventDto,
                verifier.checkUser(userId),
                verifier.checkCategory(newEventDto.getCategory()),
                verifier.validTimeCreatedOn(LocalDateTime.now(), newEventDto.getEventDate(), 2));

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public EventDto getEventByUserFullInfo(Integer userId, Integer eventId) {
        return eventMapper.toDto(eventRepository.findEventByIdAndInitiator_Id(
                        verifier.checkEvent(eventId).getId(),
                        verifier.checkUser(userId).getId())
                .orElseThrow(() -> new NotFoundException("Вероятно что данное событие создавали не вы")));
    }

    @Override
    public List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);
        return eventRepository.findEventsByInitiator_Id(verifier.checkUser(userId).getId(), page).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDto getEventById(Integer eventId, HttpServletRequest request) {
        Event event = verifier.checkPublishedEvent(eventId);
        event.setViews(getView(event));
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Override
    @Transactional
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size, HttpServletRequest request) {
        Pageable page = Page.paged(from, size, sort);

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ValidTimeException("Обратите внимание: Дата и время не раньше которых должно произойти событие," +
                        " не может быть позже чем дата и время не позже которых должно произойти событие...");
            }
        }

        Filter filter = Filter.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .build();
        Predicate predicate = getPredicates(filter);

        List<Event> events = eventRepository.findAll(predicate, page).stream()
                .collect(Collectors.toList());
        if (events.isEmpty()) {
            throw new NotFoundException("Событий не найдено");
        }

        hits(events, request);

        events.forEach(event -> event.setViews(getView(event)));

        eventRepository.saveAll(events);

        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> findEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);
        Filter filter = Filter.builder()
                .users(users)
                .states(states != null ? states.stream()
                        .map(state -> EventStatus.valueOf(state.toUpperCase()))
                        .collect(Collectors.toList()) : null)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        Predicate predicate = QPredicates.builder()
                .add(filter.getUsers(), QEvent.event.initiator.id::in)
                .add(filter.getStates(), QEvent.event.state::in)
                .add(filter.getCategories(), QEvent.event.category.id::in)
                .add(filter.getRangeStart(), QEvent.event.eventDate::goe)
                .add(filter.getRangeEnd(), QEvent.event.eventDate::loe)
                .buildAnd();

        log.debug("События найдены");

        return predicate != null ?
                eventRepository.findAll(predicate, page).stream()
                        .map(eventMapper::toDto)
                        .collect(Collectors.toList()) :
                eventRepository.findAll(page).stream()
                        .map(eventMapper::toDto)
                        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getRequestsByUser(Integer userId, Integer eventId) {
        log.debug("Найдены запросы на участие");
        return requestRepository.findParticipationRequestsByEvent_IdAndEvent_Initiator_Id(eventId, userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatusRequestsByUser(Integer userId, Integer eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        List<ParticipationRequest> requestList = requestRepository.findParticipationRequestsByEvent_IdAndEvent_Initiator_Id(eventId, userId);

        if (requestList.isEmpty()) {
            throw new NotFoundException("Событие не найдено или недоступно");
        }

        Event event = verifier.checkEvent(eventId);

        for (ParticipationRequest request : requestList) {
            boolean isUnlimitedParticipantsOrModerationDisabled = (request.getEvent().getParticipantLimit() == 0) ||
                    !request.getEvent().getRequestModeration();
            boolean isParticipantLimitReached = request.getEvent().getConfirmedRequests() >= request.getEvent().getParticipantLimit();
            boolean isRequestPending = request.getState().equals(ParticipationRequestStatus.PENDING);

            if (isUnlimitedParticipantsOrModerationDisabled) {
                request.setState(ParticipationRequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else if (isRequestPending) {
                if (isParticipantLimitReached) {
                    request.setState(ParticipationRequestStatus.REJECTED);
                } else {
                    request.setState(eventRequestStatusUpdateRequest.getStatus());
                    if (request.getState().equals(ParticipationRequestStatus.CONFIRMED)) {
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    }
                }
            } else {
                throw new ConflictException("Нарушение целостности данных.");
            }
        }

        List<ParticipationRequestDto> requestsConfirmed = new ArrayList<>();
        List<ParticipationRequestDto> requestsRejected = new ArrayList<>();

        eventRepository.save(event);
        requestRepository.saveAll(requestList);

        for (ParticipationRequest request : requestList) {
            if (request.getState().equals(ParticipationRequestStatus.CONFIRMED)) {
                requestsConfirmed.add(requestMapper.toDto(request));
            }
            if (request.getState().equals(ParticipationRequestStatus.REJECTED)) {
                requestsRejected.add(requestMapper.toDto(request));
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestsConfirmed)
                .rejectedRequests(requestsRejected)
                .build();
    }

    @Override
    public EventDto changeEventsByUser(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = verifier.checkEvent(eventId);

        if (event.getState().equals(EventStatus.PENDING) || event.getState().equals(EventStatus.CANCELED)) {
            if (updateEventUserRequest.getEventDate() != null) {
                verifier.validTimeEventDate(LocalDateTime.now(), updateEventUserRequest.getEventDate(), 2);
                event.setEventDate(updateEventUserRequest.getEventDate());
            }
            if (updateEventUserRequest.getAnnotation() != null) {
                event.setAnnotation(updateEventUserRequest.getAnnotation());
            }
            if (updateEventUserRequest.getCategory() != null) {
                event.setCategory(categoryMapper.toEntity(updateEventUserRequest.getCategory()));
            }
            if (updateEventUserRequest.getDescription() != null) {
                event.setDescription(updateEventUserRequest.getDescription());
            }
            if (updateEventUserRequest.getLocation() != null) {
                event.setLocation(updateEventUserRequest.getLocation());
            }
            if (updateEventUserRequest.getPaid() != null) {
                event.setPaid(updateEventUserRequest.getPaid());
            }
            if (updateEventUserRequest.getParticipantLimit() != null) {
                event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
            }
            if (updateEventUserRequest.getRequestModeration() != null) {
                event.setRequestModeration(updateEventUserRequest.getRequestModeration());
            }
            if (updateEventUserRequest.getStateAction() != null) {
                switch (updateEventUserRequest.getStateAction()) {
                    case SEND_TO_REVIEW:
                        event.setState(EventStatus.PENDING);
                        break;
                    case CANCEL_REVIEW:
                        event.setState(EventStatus.CANCELED);
                        break;
                }
            }
            if (updateEventUserRequest.getTitle() != null) {
                event.setTitle(updateEventUserRequest.getTitle());
            }

            log.debug("Событие обновлено");
            return eventMapper.toDto(eventRepository.save(event));
        } else {
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
    }

    @Override
    @Transactional
    public EventDto changeEvents(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = verifier.checkEvent(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = verifier.checkCategory(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationService.save(updateEventAdminRequest.getLocation());
            event.setLocation(location);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)) {
                if (event.getState().equals(EventStatus.PENDING)) {
                    event.setPublishedOn(verifier.validTimePublication(LocalDateTime.now(), event.getEventDate(), 1));
                    event.setState(EventStatus.PUBLISHED);
                    return eventMapper.toDto(eventRepository.save(event));
                } else {
                    throw new ConflictException("событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
            }
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.REJECT_EVENT)) {
                if (!event.getState().equals(EventStatus.PUBLISHED)) {
                    event.setState(EventStatus.CANCELED);
                    return eventMapper.toDto(eventRepository.save(event));
                } else {
                    throw new ConflictException("событие можно отклонить, только если оно еще не опубликовано");
                }
            }
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (event.getPublishedOn() != null) {
                event.setEventDate(verifier.validTimeEventDate(event.getPublishedOn(), updateEventAdminRequest.getEventDate(), 1));
            } else {
                event.setEventDate(verifier.validTimeEventDate(LocalDateTime.now(), updateEventAdminRequest.getEventDate(), 1));
            }
        }
        log.debug("Событие отредактировано");
        return eventMapper.toDto(eventRepository.save(event));
    }

    private Predicate getPredicates(Filter filter) {
        LocalDateTime timeNow = checkDate(filter);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(QPredicates.builder()
                .add(filter.getText(), QEvent.event.annotation::likeIgnoreCase)
                .add(filter.getText(), QEvent.event.description::likeIgnoreCase)
                .buildOr());
        predicates.add(QPredicates.builder()
                .add(filter.getCategories(), QEvent.event.category.id::in)
                .add(filter.getPaid(), QEvent.event.paid::eq)
                .add(timeNow, QEvent.event.eventDate::goe)
                .add(filter.getRangeEnd(), QEvent.event.eventDate::loe)
                .add(EventStatus.PUBLISHED, QEvent.event.state::eq)
                .buildAnd());
        return ExpressionUtils.allOf(predicates);
    }

    private LocalDateTime checkDate(Filter filter) {
        if (filter.getRangeStart() == null ||
                filter.getRangeEnd() == null) {
            return LocalDateTime.now();
        } else {
            return filter.getRangeStart();
        }
    }

    private Integer getView(Event event) {
        ResponseEntity<Object> response = client.stats(event.getCreatedOn().toString().replace("T", " ").substring(0, 19),
                event.getEventDate().toString().replace("T", " ").substring(0, 19),
                "/events/" + event.getId(),
                true);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            String body = Objects.requireNonNull(response.getBody()).toString()
                    .replace("[{", "{\"")
                    .replace("}]", "\"}")
                    .replace("=", "\":\"")
                    .replace(", ", "\",\"");

            ViewStats viewStats = gson.fromJson(body, ViewStats.class);
            return viewStats.getHits().intValue();
        }
        return event.getViews();
    }

    private void hits(List<Event> events, HttpServletRequest request) {
        client.hits(events.stream().map(Event::getId).collect(Collectors.toList()), request);
    }

}
