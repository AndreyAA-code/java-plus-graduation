package ru.practicum.dto.locations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.LocationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private LocationType type;
}
