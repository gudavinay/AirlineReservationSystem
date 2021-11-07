/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vinayguda
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Reservation")
public class Reservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String reservationNumber; // primary key
    private String origin;
    private String destination;  
    private int price;
    
	@OneToOne(targetEntity=Passenger.class, cascade=CascadeType.DETACH)
	private Passenger passenger;
	@ManyToMany(targetEntity=Flight.class)
	private List<Flight> flights;

	public Reservation(){

	}

	public Reservation(int id, String reservationNumber, String origin, String destination, int price, Passenger passenger) {
		this.reservationNumber = reservationNumber;
		this.origin = origin;
		this.destination = destination;
		this.price = price;
		this.passenger = passenger;
	}

	public String getReservationNumber() {
		return reservationNumber;
	}

	public void setReservationNumber(String reservationNumber) {
		this.reservationNumber = reservationNumber;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
}
