package ru.practicum.main.requests.cotroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService participationRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto createParticipationRequest(@PathVariable(name = "userId") Integer userId,
                                                              @RequestParam(name = "eventId") Integer eventId) {
        log.debug("Добавление запроса от текущего пользователя на участие в событии");
        return participationRequestService.createRequestsByUserOtherEvents(userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserOtherEvents(@PathVariable(name = "userId") Integer userId) {
        log.debug("Получение информации о заявках текущего пользователя на участие в чужих событиях");
        return participationRequestService.getRequestsByUserOtherEvents(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(path = "{requestId}/cancel")
    public ParticipationRequestDto cancelRequestsByUserOtherEvents(@PathVariable(name = "userId") Integer userId,
                                                                   @PathVariable(name = "requestId") Integer requestId) {
        log.debug("Отмена своего запроса на участие в событии");
        return participationRequestService.cancelRequestsByUserOtherEvents(userId, requestId);
    }
}
