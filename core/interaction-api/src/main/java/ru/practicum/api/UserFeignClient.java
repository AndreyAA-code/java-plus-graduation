package ru.practicum.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequestDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@FeignClient(name = "user-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    @PostMapping
    UserDto createUser(@RequestBody NewUserRequestDto userRequestDto);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);
}