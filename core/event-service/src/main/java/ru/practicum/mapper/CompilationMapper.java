package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.compilation.CompilationDto;
import ru.practicum.models.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation entity);
}
