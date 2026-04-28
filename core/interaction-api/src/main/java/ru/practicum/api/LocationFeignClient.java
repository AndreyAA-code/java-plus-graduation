package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;

import java.util.List;

@FeignClient(name = "location-service", fallback = LocationFeignClientFallback.class)
public interface LocationFeignClient {

    @GetMapping("/internal/locations/{id}")
    LocationResponseDto getLocationById(@PathVariable("id") Long id);

    @GetMapping("/internal/locations/search/near")
    List<ShortLocationResponseDto> findLocationsNear(@RequestParam Double lat,
                                                     @RequestParam Double lon,
                                                     @RequestParam Double radius);
}