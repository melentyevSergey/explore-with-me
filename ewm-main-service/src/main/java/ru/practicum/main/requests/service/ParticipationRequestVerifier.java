package ru.practicum.main.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;
import ru.practicum.main.event.service.EventVerifier;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.requests.repository.ParticipationRequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserVerifier;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipationRequestVerifier {
    private final ParticipationRequestRepository repository;
    private final UserVerifier userVerifier;
    private final EventVerifier eventVerifier;

    public ParticipationRequest checkParticipationRequest(Integer requestId, Integer userId) {
        return repository.findParticipationRequestByIdAndRequester_Id(requestId, userId).orElseThrow(() ->
                new NotFoundException("Запрос не найден или недоступен"));
    }

    User checkUser(Integer userId) {
        return userVerifier.checkUser(userId);
    }

    public Event checkAbilityToParticipationCreateRequest(Integer eventId, Integer userId) {
        Event event = eventVerifier.checkEvent(eventId);

        if (event.getState().equals(EventStatus.PENDING) || event.getState().equals(EventStatus.CANCELED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        List<ParticipationRequest> existingRequests = repository.findParticipationRequestsByRequester_Id(userId);

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
}
