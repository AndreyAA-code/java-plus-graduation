package ru.practicum.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.user.NewUserRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("Fallback: getUsers for ids = " + ids);
        return Collections.emptyList();
    }

    @Override
    public UserDto createUser(NewUserRequestDto userRequestDto) {
        log.info("Fallback: createUser");
        return UserDto.builder()
                .id(0L)
                .name("Default User")
                .email("default@user.com")
                .build();
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Fallback: deleteUser");
        log.info("Fallback: deleteUser");
    }

    @Override
    public UserShortDto getUserById(Long userId) {
        log.info("Fallback: getUserById");
        UserShortDto defaultUser = new UserShortDto();
        defaultUser.setId(userId);
        defaultUser.setName("Default User");
        log.info("Fallback: getUserById");
        return defaultUser;
    }
}