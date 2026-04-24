package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

@FeignClient(name = "user-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    @GetMapping("/api/v1/admin/users")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody NewUserRequestDto userRequestDto);

    @DeleteMapping("/api/v1/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long userId);

    @GetMapping("/api/v1/admin/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    UserShortDto getUserById(@PathVariable Long userId);

}