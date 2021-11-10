package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpe275.AirlineReservationSystem.entity.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Integer> {


	Optional<Flight> getFlightByFlightNumber(String flightNumber);
	void deleteByFlightNumber(String flightNumber);
}
