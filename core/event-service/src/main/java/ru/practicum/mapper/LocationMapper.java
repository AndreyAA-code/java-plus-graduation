package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.NewLocationDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;
import ru.practicum.dto.locations.UpdateLocationDto;
import ru.practicum.models.Location;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    LocationResponseDto toFullResponseDto(Location location);

    ShortLocationResponseDto toShortResponseDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location toLocation(NewLocationDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = false)
    void updateFromDto(UpdateLocationDto dto, @MappingTarget Location entity);
}
