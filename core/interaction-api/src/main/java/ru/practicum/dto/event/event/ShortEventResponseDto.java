package ru.practicum.dto.event.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.util.Patterns.TIMESTAMP_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortEventResponseDto {

    private String annotation;

    private CategoryDto category;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LatLonDto location;

    private Boolean paid;

    private String title;

    private Integer confirmedRequests;

    private Long id;

    private Long views;
}
