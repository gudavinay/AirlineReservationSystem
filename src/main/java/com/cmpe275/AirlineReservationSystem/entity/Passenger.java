/**
 * 
 */
package com.cmpe275.AirlineReservationSystem.entity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * @author vinayguda
 *
 */

@Entity
@Table(name = "Passenger")
public class Passenger {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
    private String firstname;
    
    private String lastname;
    
    private int age;
    
    private String gender;

    private String phone;

	@OneToMany(targetEntity=Reservation.class, cascade=CascadeType.ALL)
	@JsonIgnoreProperties({"passenger", "price","flights"})
	private List<Reservation> reservation;
	
	@ManyToMany(targetEntity=Flight.class, cascade=CascadeType.ALL)
	@JsonIgnoreProperties({"price","seatsLeft","description","plane","passengers"})
	private List<Flight> flight;

	public Passenger(){};
	
	public Passenger(String firstname, String lastname, int age, String gender, String phone) {
		this.lastname = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Reservation> getReservation() {
		return reservation;
	}

	public void setReservation(List<Reservation> reservation) {
		this.reservation = reservation;
	}

	public List<Flight> getFlight() {
		return flight;
	}

	public void setFlight(List<Flight> flight) {
		this.flight = flight;
	}
}
