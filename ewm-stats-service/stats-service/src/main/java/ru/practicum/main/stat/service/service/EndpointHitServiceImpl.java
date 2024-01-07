package ru.practicum.main.stat.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.stat.dto.EndpointHitDto;
import ru.practicum.main.stat.dto.ViewStats;
import ru.practicum.main.stat.service.exception.TimestampException;
import ru.practicum.main.stat.service.mapper.EndpointHitMapper;
import ru.practicum.main.stat.service.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    @Transactional
    public EndpointHitDto addHit(EndpointHitDto hitDto) {
        log.debug("Записи статистики успешно добавлены.");
        return mapper.toDto(repository.save(mapper.toEntity(hitDto)));
    }

    @Override
    @Transactional
    public List<ViewStats> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> list = new ArrayList<>();

        if (start.isAfter(end)) {
            throw new TimestampException("Время начала диапазона поиска не может быть позже времени конца.");
        }

        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                list.addAll(repository.findViewStatsByDateTimeAndUriAndUnique(start, end, uris));
            } else {
                list.addAll(repository.findViewStatsByDateTimeAndUri(start, end, uris));
            }
        } else {
            if (unique) {
                list.addAll(repository.findViewStatsByDateTimeAndUnique(start, end));
            } else {
                list.addAll(repository.findViewStatsByDateTime(start, end));
            }
        }
        return list.stream().sorted(Comparator.comparing(ViewStats::getHits).reversed()).collect(Collectors.toList());
    }
}
