package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.NewLocationDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;
import ru.practicum.dto.locations.UpdateLocationDto;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Location;
import ru.practicum.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository repository;
    private final LocationMapper mapper;
    private final StatsClient statsClient;

    @Override
    public List<LocationResponseDto> findAllFull(Pageable pageable) {
        log.info("Find all locations - returns full information");
        return repository.findAll(pageable).stream()
                .map(mapper::toFullResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LocationResponseDto findByIdFull(Long locationId, HttpServletRequest request) {
        log.info("Find location by id {} - returns full information", locationId);
        Location location = repository.findById(locationId)
                .orElseThrow(() -> {
            return new NoSuchElementException("Location with id " + locationId + " notFound");
        });
        sendHit(locationId, request);
        return mapper.toFullResponseDto(location);
    }

    @Override
    public List<ShortLocationResponseDto> findAllShort(Pageable pageable) {
        log.info("Find all locations - returns short information");
        return repository.findAll(pageable).stream()
                .map(mapper::toShortResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShortLocationResponseDto findByIdShort(Long locationId, HttpServletRequest request) {
        log.info("Find location by id {} - returns short information", locationId);
        Location location = repository.findById(locationId)
                .orElseThrow(() -> {
                    return new NoSuchElementException("Location with id " + locationId + " notFound");
                });
        sendHit(locationId, request);
        return mapper.toShortResponseDto(location);
    }

    @Override
    @Transactional
    public LocationResponseDto create(NewLocationDto dto) {
        log.info("Create new location {}", dto);
        Location location = mapper.toLocation(dto);
        return mapper.toFullResponseDto(repository.save(location));
    }

    @Override
    @Transactional
    public LocationResponseDto update(Long locationId, UpdateLocationDto dto) {
        log.info("Update location {}", dto);
        Location existingLocation = repository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Location with id=" + locationId + " not found"));
        mapper.updateFromDto(dto, existingLocation);
        return mapper.toFullResponseDto(existingLocation);
    }

    @Override
    @Transactional
    public void delete(Long locationId) {
        log.info("Delete location {}", locationId);
        Location location = repository.findById(locationId)
                .orElseThrow(() -> {
                    return new NoSuchElementException("Location with id " + locationId + " notFound");
                });
        repository.deleteById(locationId);
    }

    @Override
    public LocationResponseDto findById(Long id) {
        log.info("Find location by id {}", id);
        Location location = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location with id " + id + " not found"));
        return mapper.toFullResponseDto(location);
    }

    public List<ShortLocationResponseDto> findNear(Double lat, Double lon, Double radius) {
        log.info("Finding locations near lat={}, lon={}, radius={} (parameter radius is ignored, using DB radius)", lat, lon, radius);

        return repository.findLocationsContainingPoint(lat, lon)
                .stream()
                .map(mapper::toShortResponseDto)
                .collect(Collectors.toList());
    }

    private void sendHit(Long locationId, HttpServletRequest request) {
        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .app("location-service")
                    .uri("/locations/" + locationId)
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();

            statsClient.hit(hit);
            log.debug("Location view recorded: {}", locationId);
        } catch (Exception e) {
            log.error("Failed to record location view for id {}: {}", locationId, e.getMessage());
        }
    }
}
