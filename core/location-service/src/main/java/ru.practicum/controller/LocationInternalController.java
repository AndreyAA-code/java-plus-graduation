package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;
import ru.practicum.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/internal/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationInternalController {

    private final LocationService locationService;

    @GetMapping("/{id}")
    public LocationResponseDto getLocationById(@PathVariable Long id) {
        log.info("Internal: get location by id {}", id);
        return locationService.findById(id);
    }

    @GetMapping("/search/near")
    public List<ShortLocationResponseDto> findLocationsNear(@RequestParam Double lat,
                                                            @RequestParam Double lon,
                                                            @RequestParam Double radius) {
        log.info("Internal: find locations near lat={}, lon={}, radius={}", lat, lon, radius);
        return locationService.findNear(lat, lon, radius);
    }

    @GetMapping
    public List<ShortLocationResponseDto> getAllLocations(@RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Internal: get all locations");
        return locationService.findAllShort(PageRequest.of(from / size, size, Sort.by("id").ascending()));
    }
}