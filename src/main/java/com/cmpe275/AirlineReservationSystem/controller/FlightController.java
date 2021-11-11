/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.Util.ExceptionHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cmpe275.AirlineReservationSystem.Util.BadRequest;

import com.cmpe275.AirlineReservationSystem.service.FlightService;

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
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam(value="xml", required=false) String xml
	) {
		try{
			return flightService.getFlightByNumber(flightNumber);
		} catch (Exception ex){
			return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(400, ex.getMessage())));
		}

	}

	@RequestMapping(value="/airline/{flightNumber}", method=RequestMethod.DELETE
			, produces={"application/json", "application/xml"})
	public ResponseEntity<?> deleteFlight(
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam(value="xml", required=false) String xml
	) {
		try{
			return flightService.deleteFlight(flightNumber);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(400, ex.getMessage())));
		}
	}

	@RequestMapping(value="/flight/{flightNumber}", method=RequestMethod.POST
			, produces={"application/json", "application/xml"})
	public ResponseEntity<?> updateFlight(
			@PathVariable("flightNumber") String flightNumber,
			@RequestParam("price") int price,
			@RequestParam("origin") String origin,
			@RequestParam("destination") String destination,
			@RequestParam("departureTime")  String departureTime,
			@RequestParam("arrivalTime")  String arrivalTime,
			@RequestParam("description") String description,
			@RequestParam("capacity") int capacity,
			@RequestParam("model") String model,
			@RequestParam("manufacturer") String manufacturer,
			@RequestParam("yearOfManufacture") int yearOfManufacture,
			@RequestParam(value="xml", required=false) String xml
	) {
		try{
			return flightService.updateFlight(flightNumber, price, origin, destination, departureTime,
					arrivalTime, description, capacity, model, manufacturer, yearOfManufacture);
		} catch (Exception ex){
			return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(400, ex.getMessage())));
		}
	}
}
