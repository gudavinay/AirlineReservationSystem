/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
import com.cmpe275.AirlineReservationSystem.service.FlightService;

/**
 * @author vinayguda
 *
 */
@RestController
public class FlightController {
	
	@Autowired
	private FlightService flightService;

	@PostMapping("/addFlight")
	public Flight addFlight(@RequestBody Flight flight) {
		return flightService.saveFlight(flight);
	}
	
	@PostMapping("/addFlights")
	public List<Flight> addFlights(@RequestBody List<Flight> flight) {
		return flightService.saveFlights(flight);
	}
	
	@GetMapping("/getFlightById/{id}")
	public Flight getFlight(@PathVariable int id) {
		return flightService.getFlightById(id);
	}
	
	@GetMapping("/getFlights")
	public List<Flight> getFlights() {
		return flightService.getFlights();
	}
	
	
}
