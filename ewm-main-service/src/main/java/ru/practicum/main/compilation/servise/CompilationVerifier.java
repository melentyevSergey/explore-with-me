package ru.practicum.main.compilation.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventVerifier;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationVerifier {
    private final CompilationRepository repository;
    private final EventVerifier eventVerifier;

    Compilation checkCompilation(Integer compilationId) {
        return repository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Подборка с идентификатором =%d не найдена.", compilationId)));
    }

    List<Event> checkEvents(List<Integer> eventIds) {
        return eventVerifier.checkEvents(eventIds);
    }
}
