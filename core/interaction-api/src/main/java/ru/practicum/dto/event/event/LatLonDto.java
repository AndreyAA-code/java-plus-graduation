package ru.practicum.dto.event.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatLonDto {
    @NotNull
    @Positive
    private double lat;

    @NotNull
    @Positive
    private double lon;
}
