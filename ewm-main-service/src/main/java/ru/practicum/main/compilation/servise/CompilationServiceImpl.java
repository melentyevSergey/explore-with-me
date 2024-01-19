package ru.practicum.main.compilation.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.utility.Page;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;
    private final CompilationVerifier verifier;

    @Override
    @Transactional
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        return mapper.toDto(compilationRepository.save(mapper.toEntity(newCompilationDto)));
    }

    @Override
    @Transactional
    public void deleteCompilations(Integer compId) {
        compilationRepository.deleteById(verifier.checkCompilation(compId).getId());
    }

    @Override
    @Transactional
    public CompilationDto changeCompilations(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldCompilations = verifier.checkCompilation(compId);

        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            List<Event> eventList = verifier.checkEvents(updateCompilationRequest.getEvents());
            oldCompilations.setEvents(eventList);
        }
        if (updateCompilationRequest.getPinned() != null) {
            oldCompilations.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isEmpty()) {
            oldCompilations.setTitle(updateCompilationRequest.getTitle());
        }
        log.debug("Подборка обновлена");
        return mapper.toDto(compilationRepository.save(oldCompilations));
    }

    @Override
    public CompilationDto getCompilationsById(Integer compId) {
        log.debug("Подборка событий найдена");
        return mapper.toDto(verifier.checkCompilation(compId));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);
        log.debug("Подборка событий найдена");
        return pinned == null ?
                compilationRepository.findAll(page).stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList()) :
                compilationRepository.findAllByPinned(pinned, page).stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
    }


}
