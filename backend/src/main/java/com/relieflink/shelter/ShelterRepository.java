package com.relieflink.shelter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    @Query("SELECT s FROM Shelter s JOIN FETCH s.location")
    List<Shelter> findAllWithLocation();
}
