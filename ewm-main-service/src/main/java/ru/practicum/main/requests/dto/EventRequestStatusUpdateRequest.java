package ru.practicum.main.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.requests.model.ParticipationRequestStatus;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private ParticipationRequestStatus status;
}
