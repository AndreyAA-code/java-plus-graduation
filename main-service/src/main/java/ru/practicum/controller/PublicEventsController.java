package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.event.EventResponseDto;
import ru.practicum.dto.event.EventSearchCriteria;
import ru.practicum.dto.event.ShortEventResponseDto;
import ru.practicum.service.event.EventService;
import ru.practicum.stats.client.StatsClient;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private static final String MAIN_SERVICE = "ewm-main-service";

    private final EventService service;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShortEventResponseDto> findAll(@ModelAttribute EventSearchCriteria criteria, HttpServletRequest req) throws Exception {
        List<ShortEventResponseDto> res = service.find(criteria);
        saveHit(req);
        return res;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEvent(@PathVariable Long id, HttpServletRequest req) {
        EventResponseDto res = service.get(id);
        saveHit(req);
        return res;
    }

    private void saveHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp(MAIN_SERVICE);
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClient.hit(endpointHitDto);
    }
}
