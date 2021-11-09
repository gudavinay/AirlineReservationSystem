package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
	@Autowired
	private PassengerRepository passengerRepository;
	
	@Autowired
	private FlightRepository flightRepository;

    public ResponseEntity<?> getReservation(String id){
        Optional<Reservation> existingRes=reservationRepository.findById(id);
        if(existingRes.isPresent()){
            Reservation reservation = existingRes.get();
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation with number "+id+" does not exist");
        }
    }
    
   public ResponseEntity<?> createReservation(String passengerId, List<String> flightNumbers) {
    	Optional<Passenger> passenger = passengerRepository.findById(passengerId);
    	if(passenger.isPresent() && !CollectionUtils.isEmpty(flightNumbers)) {
    		if(flightNumbers.size()>1) {
    			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation( passengerId,flightNumbers);
        		if(isFirstTimeOverlap) {
        			//throw exception
        		}
        		boolean isSecondTimeOverlap=isTimeOverlapForSamePerson( passengerId,flightNumbers);
        		if(isSecondTimeOverlap) {
        			//throw exception
        		}
    		}
    		
    		//check for capacity
    		//if everything good
    		//Reservation res= new Reservation( origin, destination, price, passenger);
    				
    	}
    	
		return null;
	
    }
    
    public ResponseEntity<?> updateReservation( String number, List<String> flightsAdded, List<String> flightsRemoved ) {
		return null;
    	
    }
    
    //need to rework
    public ResponseEntity<?> cancelReservation( String reservationNumber) {
    	Reservation res=reservationRepository.findByReservationNumber(reservationNumber);
        if(res !=null){
        	reservationRepository.delete(res);
        	res.getPassenger().getReservation().remove(res);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation with number "+reservationNumber+" does not exist");
        }
    	
    }
    
    private  boolean isTimeOverlapWithinReservation(String passengerId, List<String> flightNumbers) {
    	
    	List<Flight> flightList = getFlightList(flightNumbers);
    	for(int i=0;i <flightList.size(); i++) {
    		for(int j=i+1; j<flightList.size();j++ ) {
    			Date startDate1 = flightList.get(i).getDepartureTime();
    			Date endDate1 =  flightList.get(i).getArrivalTime();
    			Date startDate2 = flightList.get(j).getDepartureTime();
    			Date endDate2 = flightList.get(j).getArrivalTime();
    			 if (startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0) {
    				 return true; 
    			 }
    		}
    		
    	}
    	
    	return false;
    	
    }
    
    private boolean isTimeOverlapForSamePerson(String passengerId, List<String> flightNumbers) {
    	//given flight list
    	List<Flight> givenFlightList = getFlightList(flightNumbers);
    	//get reservations for passenger
    	Optional<Passenger> passenger = passengerRepository.findById(passengerId);
    	List<Reservation> reservationList = passenger.get().getReservation();
    	//get flight list from current reservations
    	List<Flight> currentFlightList=new ArrayList<Flight>();
		for(Reservation res:reservationList){
			for(Flight flight:res.getFlights()){
				currentFlightList.add(flight);
			}
		}
		
		//check two lists for interval overlap
		for(int i=0; i < givenFlightList.size(); i++) {
			for(int j=0;j<currentFlightList.size();j++) {
				Date startDate1 = givenFlightList.get(i).getDepartureTime();
    			Date endDate1 =  givenFlightList.get(i).getArrivalTime();
    			Date startDate2 = currentFlightList.get(j).getDepartureTime();
    			Date endDate2 = currentFlightList.get(j).getArrivalTime();
    			 if (startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0) {
    				 return true; 
    			 }
			}
		}
    	
    	return false;
    }
   
    public Flight checkAvailSeats(List<Flight> flightList){
		for(Flight flight : flightList){
			if(flight.getSeatsLeft() <= 0) return flight;
		}
		return null;
	}
    
    private List<Flight> getFlightList(List<String> flightNumbers){
    	List<Flight> flightList = new ArrayList<>();
    	for(String flightNumber: flightNumbers ) {
    		Optional<Flight> flight=flightRepository.getByFlightNumber(flightNumber);
    		if(flight.isPresent()) {
    			flightList.add(flight.get());
    		}
    	}
    	return flightList;
    	
    }
}
