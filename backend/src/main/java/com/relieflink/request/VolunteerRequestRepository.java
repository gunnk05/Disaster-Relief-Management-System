package com.relieflink.request;

import com.relieflink.common.enums.VolunteerRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRequestRepository extends JpaRepository<VolunteerRequest, Long> {

    @Query("SELECT vr FROM VolunteerRequest vr " +
           "JOIN FETCH vr.request r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.resource " +
           "JOIN FETCH r.location " +
           "WHERE vr.volunteer.id = :volunteerId")
    List<VolunteerRequest> findAllByVolunteerId(@Param("volunteerId") Long volunteerId);

    Optional<VolunteerRequest> findByRequestIdAndVolunteerIdAndStatus(
            Long requestId, Long volunteerId, VolunteerRequestStatus status);

    Optional<VolunteerRequest> findByRequestId(Long requestId);

    void deleteByRequestId(Long requestId);
}
