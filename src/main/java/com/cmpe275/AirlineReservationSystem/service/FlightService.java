/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.cmpe275.AirlineReservationSystem.entity.Plane;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
import com.cmpe275.AirlineReservationSystem.repository.PlaneRepository;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author vinayguda
 *
 */
@Service
public class FlightService {
	
	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private PlaneRepository planeRepository;

	public ResponseEntity<?> getFlightByNumber(String flightNumber){
		Optional<Flight> res = flightRepository.getByFlightNumber(flightNumber);
		if(res.isPresent()) {
			Flight flight = res.get();
			return new ResponseEntity<>(flight, HttpStatus.OK);
		}else{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, the requested flight with number "+flightNumber+" does not exist");
		}
	}

	// Still need to check for overlapping flight times
	public ResponseEntity<?> updateFlight(String flightNumber, int price, String origin, String destination, Date departureTime
			, Date arrivalTime, String description, int capacity, String model, String manufacturer, int yearOfManufacture){
		Optional<Flight> res = flightRepository.getByFlightNumber(flightNumber);
		Flight flight = null;
		Plane plane = null;
		if(res.isPresent()) {
			flight = res.get();
			List<Reservation> reservationList = flightRepository.getReservationsByFlightNumber(flightNumber);
			if(reservationList.size() > capacity){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "target capacity less than active reservations");
			}
			plane = flight.getPlane();
			plane.setCapacity(capacity);
			plane.setModel(model);
			plane.setManufacturer(manufacturer);
			plane.setYearOfManufacture(yearOfManufacture);
			flight.setSeatsLeft(capacity);
		}else{
			plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
			flight = new Flight(flightNumber, price, origin, destination, departureTime, arrivalTime
					, capacity, description, null, new ArrayList<>());
		}
		plane = planeRepository.save(plane);
		flight.setPlane(plane);
		flight = flightRepository.save(flight);
		return new ResponseEntity<>(flight, HttpStatus.OK);
	}

	public ResponseStatusException deleteFlight(String flightNumber){
		Optional<Flight> res = flightRepository.getByFlightNumber(flightNumber);
		if(res.isPresent()){
			Flight flight = res.get();
			List<Reservation> reservationList = flightRepository.getReservationsByFlightNumber(flightNumber);
			if(reservationList.size() > 0){
				return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Flight with number"+ flightNumber +"cannot be deleted");
			} else {
				flightRepository.delete(flight);
				return new ResponseStatusException(HttpStatus.OK, "Flight with number"+ flightNumber +"is deleted successfully");
			}
		}else{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, the requested flight with number "+flightNumber+" does not exist");
		}
	}

}
