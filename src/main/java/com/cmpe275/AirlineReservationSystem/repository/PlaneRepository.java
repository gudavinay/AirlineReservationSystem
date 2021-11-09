package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaneRepository extends JpaRepository<Plane, String> {
    Optional<Plane> findById(String id);
}
