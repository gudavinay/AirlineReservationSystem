package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.*;

@Service
public class PassengerService {
	@Autowired
	private PassengerRepository passengerRepository;

	 @Autowired
	 private ReservationRepository reservationRepository;

	public ResponseEntity<?> createPassenger(String firstname, String lastname, String age, String gender,
			String phone) {
		Passenger isPassengerExists = passengerRepository.findByPhone(phone);
		if ( isPassengerExists == null ) {
			Passenger newPassenger = new Passenger(firstname, lastname, Integer.parseInt(age), gender, phone);
			Passenger res =passengerRepository.save(newPassenger);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Another passenger with the same number already exists.");
			//return new ResponseEntity<>("Another passenger with the same number already exists.", HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<?> updatePassenger(String id, String firstname, String lastname, String age, String gender,
			String phone) throws NotFoundException {
		Optional<Passenger> existingPass = passengerRepository.findById(id);
		if (existingPass.isPresent()) {
			try{
				Passenger isPassengerExists = passengerRepository.findByPhone(phone);
				if(isPassengerExists==null){
					Passenger passenger = existingPass.get();
					passenger.setAge(Integer.parseInt(age));
					passenger.setLastname(lastname);
					passenger.setFirstname(firstname);
					passenger.setGender(gender);
					passenger.setPhone(phone);
					Passenger res = passengerRepository.save(passenger);
					return new ResponseEntity<>(res, HttpStatus.OK);
				}else{
					throw new IllegalArgumentException("Passenger with same phone number already exist");
				}
			}catch(Exception ex){
				throw new IllegalArgumentException("Passenger with same phone number already exist");
			}
		} else {
			throw new NotFoundException("Passenger Does not exist");
		}
	}

	public void deleteReservation(Reservation reservation, Passenger passenger) {
		try {
			for (Flight flight : reservation.getFlights()) {
				updateFlightSeats(flight);
				flight.getPassengers().remove(passenger);
			}
			passenger.getReservations().remove(reservation);
			reservationRepository.delete(reservation);
		} catch (Exception e) {
			System.out.println("Exception");
		}
	}

	public void updateFlightSeats(Flight flight) {
		try {
			flight.setSeatsLeft(flight.getSeatsLeft() + 1);
		} catch (Exception e) {

		}
	}

	public ResponseEntity<?> deletePassenger(String id) throws NotFoundException {
		Optional<Passenger> existingPass = passengerRepository.findById(id);
		if (existingPass.isPresent()) {
			List<Reservation> reservations = reservationRepository.findByPassenger(existingPass.get());
			System.out.println("Reservation :" + reservations.size());

			for(Reservation reservation : reservations){
				System.out.println("Reservation :" + reservation.getPassenger());
				deleteReservation(reservation, existingPass.get());
			}
			passengerRepository.deleteById(id);
			return new ResponseEntity<>("Passenger with id" + id + " is deleted successfully ", HttpStatus.OK);
		} else {
			throw new NotFoundException("Passenger Does not exist");
		}
	}

	public ResponseEntity<?> getPassenger(String id) {
		Optional<Passenger> existingPass = passengerRepository.findById(id);
		if (existingPass.isPresent()) {
			Passenger passenger = existingPass.get();
			return new ResponseEntity<>(passenger, HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger with id "+id+" does not exist");
		}
	}
}
