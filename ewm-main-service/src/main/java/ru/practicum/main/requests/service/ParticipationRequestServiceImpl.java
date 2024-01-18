package ru.practicum.main.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.mapper.ParticipationRequestMapper;
import ru.practicum.main.requests.model.ParticipationRequest;
import ru.practicum.main.requests.model.ParticipationRequestStatus;
import ru.practicum.main.requests.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository repository;
    private final ParticipationRequestVerifier verifier;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper mapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequestsByUserOtherEvents(Integer userId, Integer eventId) {
        Event event = verifier.checkAbilityToParticipationCreateRequest(eventId, userId);
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(verifier.checkUser(userId))
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setState(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            participationRequest.setState(ParticipationRequestStatus.PENDING);
        }
        log.debug("Заявка создана");
        return mapper.toDto(repository.save(participationRequest));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserOtherEvents(Integer userId) {
        log.debug("Найдены запросы на участие");
        return repository.findParticipationRequestsByRequester_Id(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequestsByUserOtherEvents(Integer userId, Integer requestId) {
        ParticipationRequest request = verifier.checkParticipationRequest(requestId, userId);
        if (request.getState().equals(ParticipationRequestStatus.CONFIRMED)) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
        }
        request.setState(ParticipationRequestStatus.CANCELED);
        return mapper.toDto(repository.save(request));
    }
}
