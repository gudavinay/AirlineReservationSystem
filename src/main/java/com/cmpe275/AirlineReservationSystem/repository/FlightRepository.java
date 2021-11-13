package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpe275.AirlineReservationSystem.entity.Flight;

import java.util.List;
import java.util.Optional;

/**
 * This is the interface for Flight repository
 */
public interface FlightRepository extends JpaRepository<Flight, Integer> {
	/**
	 * To find a passenger by phone number
	 * @param flightNumber
	 * @return
	 */
	Optional<Flight> getFlightByFlightNumber(String flightNumber);
}
