/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cmpe275.AirlineReservationSystem.service.FlightService;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author vinayguda
 *
 */
@RestController
public class FlightController {
	
	@Autowired
	private FlightService flightService;

	@RequestMapping(value="/flight/{flightNumber}", method=RequestMethod.GET
			, produces={"application/json", "application/xml"})
	public ResponseEntity<?> getFlightByNumber(
			@RequestParam("flightNumber") String flightNumber
	) {
		return flightService.getFlightByNumber(flightNumber);
	}

	@RequestMapping(value="/airline/{flightNumber}", method=RequestMethod.DELETE
			, produces={"application/json", "application/xml"})
	public ResponseStatusException deleteFlight(
			@RequestParam("flightNumber") String flightNumber
	) {
		return flightService.deleteFlight(flightNumber);
	}

	@RequestMapping(value="/flight/{flightNumber}", method=RequestMethod.POST
			, produces={"application/json", "application/xml"})
	public ResponseEntity<?> updateFlight(
			@RequestParam("flightNumber") String flightNumber,
			@RequestParam("price") int price,
			@RequestParam("origin") String origin,
			@RequestParam("destination") String destination,
			@RequestParam("departureTime") Date departureTime,
			@RequestParam("arrivalTime") Date arrivalTime,
			@RequestParam("description") String description,
			@RequestParam("capacity") int capacity,
			@RequestParam("model") String model,
			@RequestParam("manufacturer") String manufacturer,
			@RequestParam("yearOfManufacture") int yearOfManufacture
	) {
		return flightService.updateFlight(flightNumber, price, origin, destination, departureTime,
				arrivalTime, description, capacity, model, manufacturer, yearOfManufacture);
	}
}
