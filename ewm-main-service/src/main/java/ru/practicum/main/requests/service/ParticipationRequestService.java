package ru.practicum.main.requests.service;

import ru.practicum.main.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto createRequestsByUserOtherEvents(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getRequestsByUserOtherEvents(Integer userId);

    ParticipationRequestDto cancelRequestsByUserOtherEvents(Integer userId, Integer requestId);
}
