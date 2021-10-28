/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;

/**
 * @author vinayguda
 *
 */
@Service
public class FlightService {
	
	@Autowired
	private FlightRepository repository;

	public Flight saveFlight(Flight flight) {
		return repository.save(flight);
	}
	
	public List<Flight> saveFlights(List<Flight> flight) {
		return repository.saveAll(flight);
	}

	public Flight getFlightById(int id){
		return repository.findById(id).orElse(null);
	}

	public List<Flight> getFlights(){
		return repository.findAll();
	}

}
