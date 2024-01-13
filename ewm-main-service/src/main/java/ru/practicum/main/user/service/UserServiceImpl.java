package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.utility.Page;
import ru.practicum.main.utility.Utility;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Utility utility;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        utility.checkEmploymentEmailUser(userDto.getEmail());
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
    }

    @Override
    @Transactional
    public void removeById(Integer userId) {
        userRepository.deleteById(utility.checkUser(userId).getId());
    }

    @Transactional
    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        Pageable page = Page.paged(from, size);
        if (ids != null && !ids.isEmpty()) {
            log.debug("Попытка получить пользователей");
            return userRepository.findAllById(ids).stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            log.debug("Попытка получить пользователей");
            return userRepository.findAll(page).stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }


}
