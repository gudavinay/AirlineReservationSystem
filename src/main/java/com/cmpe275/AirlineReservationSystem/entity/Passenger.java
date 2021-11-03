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
@Table(name = "Passenger")
public class Passenger {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;   // primary key
    private String firstname;
    private String lastname;
    private int age;  // Full form only (see definition below)
    private String gender;  // Full form only
    private String phone; // Phone numbers must be unique.   Full form only

	@OneToMany(targetEntity=Reservation.class, cascade=CascadeType.ALL)
	private List<Reservation> reservation;
	@ManyToMany(targetEntity=Flight.class, cascade=CascadeType.ALL)
	private List<Flight> flight;
	

}
