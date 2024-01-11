package ru.practicum.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findEventByIdAndInitiator_Id(Integer eventId, Integer userId);

    List<Event> findEventsByInitiator_Id(Integer id, Pageable page);

    Optional<Event> findEventByIdAndStateIs(Integer eventId, EventStatus eventStatus);

    List<Event> findEventsByCategory_Id(Integer catId);
}
