package ru.practicum.main.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.comment.dto.CommentByUserDto;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.annotation", target = "annotation")
    @Mapping(source = "author.name", target = "authorName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.annotation", target = "annotation")
    CommentByUserDto toByUserDto(Comment comment);
}
