package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;

import java.util.*;

import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This is the interface for Passenger repository
 */
public interface PassengerRepository extends JpaRepository<Passenger, String> {
	/**
	 * To find a passenger by phone number
	 * @param phone
	 * @return
	 */
	Passenger findByPhone(String phone);

	/**
	 * To find a passenger by id
	 * @param id
	 * @return
	 */
	Optional<Passenger> findById(String id);
}
