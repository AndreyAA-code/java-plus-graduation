package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.NewLocationDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;
import ru.practicum.dto.locations.UpdateLocationDto;

import java.util.List;

public interface LocationService {

    List<LocationResponseDto> findAllFull(Pageable pageable);

    LocationResponseDto findByIdFull(Long locationId, HttpServletRequest request);

    List<ShortLocationResponseDto> findAllShort(Pageable pageable);

    ShortLocationResponseDto findByIdShort(Long locationId, HttpServletRequest request);

    LocationResponseDto create(NewLocationDto dto);

    LocationResponseDto update(Long locationId, UpdateLocationDto dto);

    void delete(Long locationId);

    LocationResponseDto findById(Long id);

    List<ShortLocationResponseDto> findNear(Double lat, Double lon, Double radius);
}
