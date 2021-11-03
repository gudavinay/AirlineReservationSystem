/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "Flight")
public class Flight {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    private String flightNumber; // Primary key
    private int price;    // Full form only
    private String origin;
    private String destination;  
    /*  Date format: yy-mm-dd-hh, do not include minutes and seconds.
    ** Example: 2017-03-22-19
    **The system only needs to support PST. You can ignore other time zones.  
    */
    private Date departureTime;     
    private Date arrivalTime;
    private int seatsLeft;    // Full form only
    private String description;   // Full form only
	    

	@OneToOne(targetEntity=Plane.class, cascade=CascadeType.ALL)
	private Plane plane;
	@ManyToMany(targetEntity=Passenger.class)
//    @Column(name = "passenger_list")  
	private List<Passenger> passengers;
	    
	

}
