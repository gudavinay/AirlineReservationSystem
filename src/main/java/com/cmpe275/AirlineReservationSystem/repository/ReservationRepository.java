package com.cmpe275.AirlineReservationSystem.repository;


import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository  extends JpaRepository<Reservation, String> {
	Reservation findByReservationNumber(String reservationNumber);
	List<Reservation> findAllByFlightsIn(List<Flight> flights);
}
