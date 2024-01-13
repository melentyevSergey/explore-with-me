package ru.practicum.main.user.service;

import ru.practicum.main.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    void removeById(Integer userId);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);
}
