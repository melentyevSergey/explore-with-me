package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidTimeException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventVerifier {
    private final EventRepository repository;
    private final UserVerifier userVerifier;
    private final CategoryRepository categoryRepository;

    public Event checkEvent(Integer eventId) {
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с идентификатором =%d не найдено", eventId)));
    }

    public List<Event> checkEvents(List<Integer> eventIds) {
        List<Event> events = repository.findAllById(eventIds);
        if (events.size() == eventIds.size()) {
            return events;
        } else {
            throw new NotFoundException("Запрос составлен некорректно");
        }
    }

    public Event checkPublishedEvent(Integer eventId) {
        return repository.findEventByIdAndStateIs(eventId, EventStatus.PUBLISHED).orElseThrow(() ->
                new NotFoundException(String.format("Событие с идентификатором =%d не найдено или не опубликовано", eventId)));
    }

    public void checkAbilityRemoveCategory(Integer catId) {
        if (!repository.findEventsByCategory_Id(catId).isEmpty()) {
            throw new ConflictException("Данную категорию нельзя удалить т.к. она используется.");
        }
    }

    Category checkCategory(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с идентификатором =%d не найдена", catId)));
    }

    User checkUser(Integer userId) {
        return userVerifier.checkUser(userId);
    }

    LocalDateTime validTimePublication(LocalDateTime createdOn, LocalDateTime eventDate, Integer difference) {
        if (Duration.between(createdOn, eventDate).toMinutes() < Duration.ofHours(difference).toMinutes()) {
            throw new ValidTimeException(String.format("Обратите внимание: дата и время, на которые намечено событие," +
                    " не может быть раньше, чем через =%d час/часа от текущего момента", difference));
        }
        return createdOn;
    }

     LocalDateTime validTimeEventDate(LocalDateTime publishedOn, LocalDateTime eventDate, Integer difference) {
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

    LocalDateTime validTimeCreatedOn(LocalDateTime createdOn, LocalDateTime eventDate, Integer difference) {
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
}
