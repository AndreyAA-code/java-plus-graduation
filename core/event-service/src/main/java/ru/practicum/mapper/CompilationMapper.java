package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.compilation.CompilationDto;
import ru.practicum.models.Compilation;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    CompilationDto toDto(Compilation entity);
}
