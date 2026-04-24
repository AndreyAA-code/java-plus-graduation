package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.UserFeignClient;
import ru.practicum.dto.user.NewUserRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController implements UserFeignClient {
    private final UserService userService;

    @Override
    public List<UserDto> getUsers(@RequestParam (required = false) List<Long> ids,
                                  @RequestParam (defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getUsers(ids, PageRequest.of(from / size, size));
    }

   @Override
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto userRequestDto) {
        log.info("Create new user {}", userRequestDto);
        UserDto user =  userService.addUser(userRequestDto);
        log.info("Created user {}", user);
        return user;
    }

    @Override
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        userService.delete(userId);
    }

    @Override
    public UserShortDto getUserById(@PathVariable("userId") Long userId) {
        log.debug("Internal: getUser userId={}", userId);
        return userService.getUserShort(userId);
    }
}
