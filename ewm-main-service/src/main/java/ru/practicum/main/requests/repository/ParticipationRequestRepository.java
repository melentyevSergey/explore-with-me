package ru.practicum.main.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.requests.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    List<ParticipationRequest> findParticipationRequestsByEvent_IdAndEvent_Initiator_Id(
            Integer eventId, Integer userId);

    List<ParticipationRequest> findParticipationRequestsByRequester_Id(Integer userId);

    Optional<ParticipationRequest> findParticipationRequestByIdAndRequester_Id(Integer requestId, Integer userId);

}
