package ru.practicum.main.utility;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidTimeException;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.requests.repository.ParticipationRequestRepository;
import ru.practicum.main.stat.client.StatsClient;
import ru.practicum.main.stat.dto.ViewStats;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Utility {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final StatsClient client;
    private final Gson gson = new Gson();

    public User checkUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с идентификатором =%d не найден", userId)));
    }

    public Category checkCategory(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с идентификатором =%d не найдена", catId)));
    }

    public Event checkEvent(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с идентификатором =%d не найдено", eventId)));
    }

    public List<Event> checkEvents(List<Integer> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);
        if (events.size() == eventIds.size()) {
            return events;
        } else {
            throw new NotFoundException("Запрос составлен некорректно");
        }
    }

    public Event checkPublishedEvent(Integer eventId) {
        return eventRepository.findEventByIdAndStateIs(eventId, EventStatus.PUBLISHED).orElseThrow(() ->
                new NotFoundException(String.format("Событие с идентификатором =%d не найдено или не опубликовано", eventId)));
    }

    public Event checkAbilityToParticipationCreateRequest(Integer eventId, Integer userId) {
        Event event = checkEvent(eventId);

        if (event.getState().equals(EventStatus.PENDING) || event.getState().equals(EventStatus.CANCELED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        List<ParticipationRequest> existingRequests = requestRepository.findParticipationRequestsByRequester_Id(userId);

        if (existingRequests != null && !existingRequests.isEmpty()) {
            for (ParticipationRequest request : existingRequests) {
                if (Objects.equals(request.getEvent().getId(), eventId)) {
                    throw new ConflictException("Нельзя добавить повторный запрос ");
                }
            }
        }
        if (event.getParticipantLimit() != 0) {
            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException("У события достигнут лимит запросов на участие");
            }
        }
        return event;
    }

    public ParticipationRequest checkParticipationRequest(Integer requestId, Integer userId) {
        return requestRepository.findParticipationRequestByIdAndRequester_Id(requestId, userId).orElseThrow(() ->
                new NotFoundException("Запрос не найден или недоступен"));
    }

    public Compilation checkCompilation(Integer compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка с идентификатором =%d не найдена.", compilationId)));
    }

    public LocalDateTime validTimePublication(LocalDateTime createdOn, LocalDateTime eventDate, Integer difference) {
        if (Duration.between(createdOn, eventDate).toMinutes() < Duration.ofHours(difference).toMinutes()) {
            throw new ValidTimeException(String.format("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть раньше, чем через =%d час/часа от текущего момента", difference));
        }
        return createdOn;
    }

    public LocalDateTime validTimeEventDate(LocalDateTime publishedOn, LocalDateTime eventDate, Integer difference) {
        if (eventDate.isBefore(publishedOn)) {
            throw new ValidTimeException("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть в прошлом");
        }
        if (Duration.between(publishedOn, eventDate).toMinutes() < Duration.ofHours(difference).toMinutes()) {
            throw new ValidTimeException(String.format("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть раньше, чем через =%d час/часа от текущего момента", difference));
        }
        return eventDate;
    }

    public LocalDateTime validTimeCreatedOn(LocalDateTime createdOn, LocalDateTime eventDate, Integer difference) {
        if (eventDate.isBefore(createdOn)) {
            throw new ValidTimeException("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть в прошлом");
        }
        if (Duration.between(createdOn, eventDate).toMinutes() < Duration.ofHours(difference).toMinutes()) {
            throw new ValidTimeException(String.format("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть раньше, чем через =%d час/часа от текущего момента", difference));
        }
        return createdOn;
    }

    public Integer getView(Event event) {
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

    public void hits(List<Event> events, HttpServletRequest request) {
        client.hits(events.stream().map(Event::getId).collect(Collectors.toList()), request);
    }

    public void checkAbilityCreateNameCategory(String name) {
        if (!categoryRepository.findCategoryByName(name).isEmpty()) {
            throw new ConflictException("Данное название категории уже занято!");
        }
    }

    public void checkAbilityChangeNameCategory(String name, Integer catId) {
        for (Category cat : categoryRepository.findCategoryByName(name)) {
            if (!cat.getId().equals(catId)) {
                throw new ConflictException("Данное название категории уже занято!");
            }
        }
    }

    public void checkEmploymentEmailUser(String email) {
        if (!userRepository.findUserByEmail(email).isEmpty()) {
            throw new ConflictException("Данный email уже занят.");
        }
    }

    public void checkAbilityRemoveCategory(Integer catId) {
        if (!eventRepository.findEventsByCategory_Id(catId).isEmpty()) {
            throw new ConflictException("Данную категорию нельзя удалить т.к. она используется.");
        }
    }
}
