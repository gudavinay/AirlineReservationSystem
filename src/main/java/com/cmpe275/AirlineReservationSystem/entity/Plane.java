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

	public Plane(int id, String model, int capacity, String manufacturer, int yearOfManufacture) {
		this.id = id;
		this.model = model;
		this.capacity = capacity;
		this.manufacturer = manufacturer;
		this.yearOfManufacture = yearOfManufacture;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getYearOfManufacture() {
		return yearOfManufacture;
	}

	public void setYearOfManufacture(int yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}
}