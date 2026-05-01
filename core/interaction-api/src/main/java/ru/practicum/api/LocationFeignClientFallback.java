package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.locations.LocationResponseDto;
import ru.practicum.dto.locations.ShortLocationResponseDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LocationFeignClientFallback implements LocationFeignClient {

    @Override
    public LocationResponseDto getLocationById(Long id) {
        LocationResponseDto defaultLocation = new LocationResponseDto();
        defaultLocation.setId(id);
        defaultLocation.setName("Default Location");
        defaultLocation.setLatitude(0.0);
        defaultLocation.setLongitude(0.0);
        defaultLocation.setRadius(1.0);
        return defaultLocation;
    }

    @Override
    public List<LocationResponseDto> findLocationsNear(Double lat, Double lon, Double radius) {
        log.info("Fallback: findLocationsNear for lat = " + lat + ", lon = " + lon + ", radius = " + radius);
        return Collections.emptyList();
    }
}