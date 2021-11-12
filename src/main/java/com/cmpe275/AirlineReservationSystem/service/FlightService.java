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

	public ResponseEntity<?> getFlightByNumber(String flightNumber) throws NotFoundException {
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if (res.isPresent()) {
			Flight flight = res.get();
			return new ResponseEntity<>(flight, HttpStatus.OK);
		} else {
			throw new NotFoundException("Sorry, the requested flight with number " + flightNumber + " does not exist");
		}
	}

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
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(new ArrayList<Flight>() {
				{
					add(finalFlight);
				}
			});
			if (reservationList.size() > capacity) {
				throw new IllegalArgumentException("target capacity less than active reservations");
			}
			if (!checkValidUpdate(flightNumber, aTime, dTime)) {
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

	private boolean checkValidUpdate(String flightNumber, Date currentFlightArrivalTime,
			Date currentFlightDepartureTime) {
		for (Passenger passenger : passengerRepository.findAll()) {
			for (Reservation reservation : passenger.getReservations()) {
				for (Flight flight : reservation.getFlights()) {
					if (flight.getFlightNumber().equals(flightNumber))
						continue;
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

	public ResponseEntity<?> deleteFlight(String flightNumber) throws NotFoundException {
		Optional<Flight> res = flightRepository.getFlightByFlightNumber(flightNumber);
		if (res.isPresent()) {
			Flight flight = res.get();
			List<Reservation> reservationList = reservationRepository.findAllByFlightsIn(new ArrayList<Flight>() {
				{
					add(flight);
				}
			});
			if (!reservationList.isEmpty()) {
				throw new IllegalArgumentException("Flight with number " + flightNumber + " cannot be deleted");
			} else {
				flightRepository.delete(flight);
				return new ResponseEntity<>("Flight with number " + flightNumber + " is deleted successfully",
						HttpStatus.OK);
			}
		} else {
			throw new NotFoundException("Sorry, the requested flight with number " + flightNumber + " does not exist");
		}
	}
}
