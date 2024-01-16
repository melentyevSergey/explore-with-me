package ru.practicum.main.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findUserByEmail(String email);
}
