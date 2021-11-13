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
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;

/**
 * Flight service Class is to handle the operations related to Flights
 * @author Nikhil Raj Karlapudi
 * @version 1.0
 */
@Service
public class FlightService {

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private PassengerRepository passengerRepository;

	/**
	 * This method is used to fetch details of a flight.
	 * @param flightNumber
	 * @return
	 */
	public ResponseEntity<?> getFlightByNumber(String flightNumber) throws NotFoundException {
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if (res.isPresent()) {
			Flight flight = res.get();
			return new ResponseEntity<>(flight, HttpStatus.OK);
		} else {
			throw new NotFoundException("Sorry, the requested flight with number " + flightNumber + " does not exist");
		}
	}

	/**
	 * This method is used to create/update a flight.
	 * @param flightNumber
	 * @param price
	 * @param origin
	 * @param destination
	 * @param departureTime
	 * @param arrivalTime
	 * @param description
	 * @param capacity
	 * @param model
	 * @param manufacturer
	 * @param yearOfManufacture
	 * @return
	 */
	public ResponseEntity<?> updateFlight(String flightNumber, int price, String origin, String destination,
			String departureTime, String arrivalTime, String description, int capacity, String model,
			String manufacturer, int yearOfManufacture) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
		Date dTime = formatter.parse(departureTime);
		Date aTime = formatter.parse(arrivalTime);
		if (origin.equals(destination) || aTime.compareTo(dTime) <= 0) {
			throw new IllegalArgumentException("Illegal arguments entered to create/update flight.");
		}
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		Flight flight;
		Plane plane;
		if (res.isPresent()) {
			flight = res.get();
			Flight finalFlight = flight;
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {{add(finalFlight);}});
			if (reservationList.size() > capacity) {
				throw new IllegalArgumentException("target capacity less than active reservations");
			}
			if (!checkValidUpdate(flight, aTime, dTime)) {
				throw new IllegalArgumentException("flight timings overlapping with other passenger reservations");
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
		} else {
			plane = new Plane(capacity, model, manufacturer, yearOfManufacture);
			flight = new Flight(flightNumber, price, origin, destination, dTime, aTime, capacity, description, plane,
					new ArrayList<>());
		}
		flight = flightRepository.save(flight);
		return new ResponseEntity<>(flight, HttpStatus.OK);
	}

	/**
	 * This method is used to fetch details of a flight.
	 * @param flightNumber
	 * @return
	 */
	public void deleteFlight(String flightNumber) throws NotFoundException {
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if (res.isPresent()) {
			Flight flight = res.get();
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(
					new ArrayList<Flight>() {{add(flight);}});
			if (!reservationList.isEmpty()) {
				throw new IllegalArgumentException("Flight " + flightNumber + " has active reservations");
			} else {
				flightRepository.delete(flight);
				new ResponseEntity<>(HttpStatus.OK);
			}
		} else {
			throw new NotFoundException("Sorry, the requested flight with number " + flightNumber + " does not exist");
		}
	}

	/**
	 * This method is used to check if updated flight details do not collide with the any flights already
	 * reserved for passengers.
	 * @param currentFlight
	 * @param currentFlightArrivalTime
	 * @param currentFlightDepartureTime
	 * @return
	 */
	private boolean checkValidUpdate(Flight currentFlight, Date currentFlightArrivalTime,
									 Date currentFlightDepartureTime) {
		for (Passenger passenger : passengerRepository.findAll()) {
			Set<Flight> flights = new HashSet<>();
			for (Reservation reservation : passenger.getReservations()) {
				flights.addAll(reservation.getFlights());
			}
			if(flights.contains(currentFlight)){
				flights.remove(currentFlight);
				for(Flight flight: flights){
					Date flightDepartureTime = flight.getDepartureTime();
					Date flightArrivalTime = flight.getArrivalTime();
					if (currentFlightArrivalTime.compareTo(flightDepartureTime) >= 0
							&& currentFlightDepartureTime.compareTo(flightArrivalTime) <= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
