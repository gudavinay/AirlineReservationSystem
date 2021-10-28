/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	@GeneratedValue
	private int id;
	
	private String flightNumber;
	

}
