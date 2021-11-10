/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vinayguda
 *
 */

@Data
@Entity
// TODO jackson xml root element
@Table(name = "Flight")
public class Flight {
	
	@Id
	//@GeneratedValue(generator="system-uuid")
	//@GenericGenerator(name="system-uuid", strategy = "uuid")
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

    public Flight(){}

    public Flight(String flightNumber, int price, String origin, String destination, Date departureTime,
			Date arrivalTime, int seatsLeft, String description, Plane plane, List<Passenger> passengers) {
		super();
		this.flightNumber = flightNumber;
		this.price = price;
		this.origin = origin;
		this.destination = destination;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.seatsLeft = seatsLeft;
		this.description = description;
		this.plane = plane;
		this.passengers = passengers;
	}

	public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

	@Override
	public int hashCode() {
		return Objects.hash(flightNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flight other = (Flight) obj;
		return Objects.equals(flightNumber, other.flightNumber);
	}
    
    
}


