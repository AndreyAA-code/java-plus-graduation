package ru.practicum.dto.event.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.category.CategoryDto;
import ru.practicum.dto.event.event.LatLonDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.util.EventState;

import java.time.LocalDateTime;

import static ru.practicum.util.Patterns.TIMESTAMP_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventResponseDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private LatLonDto location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = TIMESTAMP_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}
