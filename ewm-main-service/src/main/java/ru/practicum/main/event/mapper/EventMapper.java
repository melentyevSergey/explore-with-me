package ru.practicum.main.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    default Event toEntity(NewEventDto newEventDto, User initiator, Category category, LocalDateTime createdOn) {
        return Event.builder()
                .id(null)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .createdOn(createdOn)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(LocalDateTime.now())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventStatus.PENDING)
                .title(newEventDto.getTitle())
                .views(0)
                .build();
    }

    EventDto toDto(Event event);

    EventShortDto toShortDto(Event event);

}
