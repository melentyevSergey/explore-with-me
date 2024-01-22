package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserVerifier {
    private final UserRepository userRepository;

    public void checkEmploymentEmailUser(String email) {
        if (!userRepository.findUserByEmail(email).isEmpty()) {
            throw new ConflictException("Данный email уже занят.");
        }
    }

    public User checkUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с идентификатором =%d не найден", userId)));
    }
}
