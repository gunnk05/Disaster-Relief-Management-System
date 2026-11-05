package com.relieflink.request;

import com.relieflink.common.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r JOIN FETCH r.user JOIN FETCH r.resource JOIN FETCH r.location WHERE r.user.id = :userId")
    List<Request> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Request r JOIN FETCH r.user JOIN FETCH r.resource JOIN FETCH r.location WHERE r.status = :status")
    List<Request> findAllByStatus(@Param("status") RequestStatus status);

    @Query("SELECT r FROM Request r JOIN FETCH r.user JOIN FETCH r.resource JOIN FETCH r.location")
    List<Request> findAllWithDetails();
}
