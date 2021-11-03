/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "Plane")
public class Plane {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String model;
    private int capacity;
    private String manufacturer;
    private int yearOfManufacture;

}
