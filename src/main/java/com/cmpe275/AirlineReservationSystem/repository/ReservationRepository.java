package com.cmpe275.AirlineReservationSystem.repository;


import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 
 * @author payalghule
 *
 */


public interface ReservationRepository  extends JpaRepository<Reservation, String> {
	/**
	 * To find the reservation by using reservationNumber
	 * @param reservationNumber
	 * @return
	 */
	Reservation findByReservationNumber(String reservationNumber);
	
	/**
	 * to find reservation using  flights list
	 * @param flights
	 * @return
	 */
	List<Reservation> findAllByFlightsIn(List<Flight> flights);
	
	/**
	 * to find the reservation using passenger
	 * @param passenger
	 * @return
	 */
	List<Reservation> findByPassenger(Passenger passenger);
}
