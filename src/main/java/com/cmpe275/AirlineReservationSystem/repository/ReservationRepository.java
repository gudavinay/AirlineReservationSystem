package com.cmpe275.AirlineReservationSystem.repository;


import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationRepository  extends JpaRepository<Reservation, String> {

	Reservation findByReservationNumber(String reservationNumber);
	
}
