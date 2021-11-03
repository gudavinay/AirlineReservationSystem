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
	private int id;
	private String reservationNumber; // primary key
    private String origin;
    private String destination;  
    private int price;
    
	@OneToOne(targetEntity=Passenger.class, cascade=CascadeType.DETACH)
	private Passenger passenger;
	@ManyToMany(targetEntity=Flight.class)
	private List<Flight> flights;
	

}
