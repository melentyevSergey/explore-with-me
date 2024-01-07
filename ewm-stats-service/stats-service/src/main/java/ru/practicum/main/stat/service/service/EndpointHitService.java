package ru.practicum.main.stat.service.service;

import ru.practicum.main.stat.dto.EndpointHitDto;
import ru.practicum.main.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {

    EndpointHitDto addHit(EndpointHitDto hitDto);

    List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
