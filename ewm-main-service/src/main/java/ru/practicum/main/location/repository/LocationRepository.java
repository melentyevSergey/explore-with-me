package ru.practicum.main.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.location.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
}
