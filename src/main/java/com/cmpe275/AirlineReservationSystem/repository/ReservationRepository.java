package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReservationRepository  extends JpaRepository<Reservation, String> {
    //List<Reservation> findByPassengerID(Passenger passenger);
}
