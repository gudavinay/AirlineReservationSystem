package com.cmpe275.AirlineReservationSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpe275.AirlineReservationSystem.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

	Flight getByFlightNumber(String flightNumber);
	
}
