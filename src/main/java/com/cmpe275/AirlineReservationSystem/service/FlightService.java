/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Plane;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
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
	private ReservationRepository reservationRepository;

	@Autowired
	private PassengerRepository passengerRepository;

	public ResponseEntity<?> getFlightByNumber(String flightNumber){
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if(res.isPresent()) {
			Flight flight = res.get();
			return new ResponseEntity<>(flight, HttpStatus.OK);
		}else{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, the requested flight with number "
					+flightNumber+" does not exist");
		}
	}

	public ResponseEntity<?> updateFlight(String flightNumber, int price, String origin, String destination
			, String departureTime, String arrivalTime, String description, int capacity, String model
			, String manufacturer, int yearOfManufacture) throws ParseException {
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Flight flight;
		Plane plane;
		Date dTime = formatter.parse(departureTime);
		Date aTime = formatter.parse(arrivalTime);
		if(res.isPresent()) {
			flight = res.get();
			Flight finalFlight = flight;
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {{add(finalFlight);}});
			if(reservationList.size() > capacity) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"target capacity less than active reservations");
			}
			flight.setPrice(price);
			flight.setOrigin(origin);
			flight.setDestination(destination);
			flight.setDepartureTime(dTime);
			flight.setArrivalTime(aTime);
			flight.setDescription(description);
			flight.setSeatsLeft(capacity);
			flight.getPlane().setCapacity(capacity);
			flight.getPlane().setModel(model);
			flight.getPlane().setManufacturer(manufacturer);
			flight.getPlane().setYearOfManufacture(yearOfManufacture);
			if(!checkValidUpdate(flight)){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"flight timings overlapping with other passenger reservations");
			}
		}else{
			plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
			flight = new Flight(flightNumber, price, origin, destination, dTime, aTime
					, capacity, description, plane, new ArrayList<>());
		}
		flight = flightRepository.save(flight);
		return new ResponseEntity<>(flight, HttpStatus.OK);
	}

	private boolean checkValidUpdate(Flight currentFlight){
		for(Passenger passenger: passengerRepository.findAll()){
			for(Reservation reservation: passenger.getReservation()){
				for(Flight flight: reservation.getFlights()){
					Date currentFlightDepartureTime = currentFlight.getDepartureTime();
					Date currentFlightArrivalTime = currentFlight.getArrivalTime();
					Date flightDepartureTime = flight.getDepartureTime();
					Date flightArrivalTime = flight.getArrivalTime();
					if((currentFlightDepartureTime.compareTo(flightDepartureTime) >= 0
							&& currentFlightDepartureTime.compareTo(flightArrivalTime) <= 0)||
							(currentFlightArrivalTime.compareTo(flightDepartureTime) >= 0)
									&& currentFlightArrivalTime.compareTo(flightArrivalTime) <= 0){
						return false;
					}
				}
			}
		}
		return true;
	}

	public ResponseStatusException deleteFlight(String flightNumber){
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if(res.isPresent()){
			Flight flight = res.get();
			System.out.println(flight);
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {{add(flight);}});
			System.out.println(reservationList.size());
			if(!reservationList.isEmpty()){
				return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Flight with number "
						+ flightNumber +" cannot be deleted");
			} else {
				flightRepository.delete(flight);
				return new ResponseStatusException(HttpStatus.OK, "Flight with number "+ flightNumber
						+" is deleted successfully");
			}
		}else{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, the requested flight with number "
					+flightNumber+" does not exist");
		}
	}

}
