package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.dto.event.event.*;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.models.Category;
import ru.practicum.models.Event;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lat", source = "newEventRequest.location.lat")
    @Mapping(target = "lon", source = "newEventRequest.location.lon")
    @Mapping(target = "state", expression = "java(ru.practicum.util.EventState.PENDING)")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "initiatorId", source = "userId")
    Event eventRequestToEvent(NewEventRequestDto newEventRequest, Category category, long userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", source = "category")
    Event updateEventField(@MappingTarget Event event, UpdateEventRequestDto req, Category category);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    @Mapping(target = "initiator", source = "userDto")
    ShortEventResponseDto eventToShortEventResponseDto(Event event, UserShortDto userDto);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    @Mapping(target = "initiator", source = "userDto")
    EventResponseDto eventToEventResponseDto(Event event, UserShortDto userDto);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "userDto")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "location.lat", source = "event.lat")
    @Mapping(target = "location.lon", source = "event.lon")
    AdminEventResponseDto toAdminEventFullDto(Event event, UserShortDto userDto);

}
