package ru.practicum.dto.event.compilation;

import lombok.Data;
import ru.practicum.dto.event.event.ShortEventResponseDto;

import java.util.Set;

@Data
public class CompilationDto {

    private Long id;

    private String title;

    private Boolean pinned;

    private Set<ShortEventResponseDto> events;
}
