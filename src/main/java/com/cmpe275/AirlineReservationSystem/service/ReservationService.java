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
    		List<Flight> flightList = getFlightList(flightNumbers);
    		if(flightList.size()>1) {
    			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation( passengerId,flightList);
        		if(isFirstTimeOverlap) {
        	    	System.out.println("Time overlap within same reservation");
        			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is time overlap between the flights within same reservation");
        		}
        		boolean isSecondTimeOverlap=isTimeOverlapForSamePerson( passengerId,flightList);
        		if(isSecondTimeOverlap) {
        			System.out.println("Time overlap within for same Person");
        			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the same person cannot have two reservations that overlap with each other");
        		}
    		}
    		//check for capacity
    		
    		if(isSeatsAvailable(flightList)) {
    			int fare=calculatePrice(flightList);
    			Reservation newReservation= new Reservation( flightList.get(0).getOrigin(), flightList.get(flightList.size()-1).getDestination(), fare, passenger.get(),flightList);
    			Reservation res =reservationRepository.save(newReservation);
    			reduceAvailableFlightSeats(flightList);
    			return new ResponseEntity<>(res, HttpStatus.OK);
    		}
	
    	}
    	
    	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
	
    }
   
 
    
    public ResponseEntity<?> updateReservation( String number, List<String> flightsAdded, List<String>  flightList  ) {
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
    
    private  boolean isTimeOverlapWithinReservation(String passengerId, List<Flight> flightList ) {
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
    
    private boolean isTimeOverlapForSamePerson(String passengerId, List<Flight> flightList) {
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
		System.out.println("existing Reservations for current passeneger: " +currentFlightList );
		//check two lists for interval overlap
		for(int i=0; i < flightList.size(); i++) {
			for(int j=0;j<currentFlightList.size();j++) {
				Date startDate1 = flightList.get(i).getDepartureTime();
    			Date endDate1 =  flightList.get(i).getArrivalTime();
    			Date startDate2 = currentFlightList.get(j).getDepartureTime();
    			Date endDate2 = currentFlightList.get(j).getArrivalTime();
    			 if (startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0) {
    				 return true; 
    			 }
			}
		}
    	
    	return false;
    }
   
    public boolean isSeatsAvailable(List<Flight> flightList){
		for(Flight flight : flightList){
			if(flight.getSeatsLeft() <= 0) return false;
		}
		return true;
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
    
    public int calculatePrice(List<Flight> flightList) {
 	   int price=0;
 	   for(Flight f:flightList ) {
 		   price+=f.getPrice();
 	   }
 	   return price;
    }
    
    public Flight reduceAvailableFlightSeats(List<Flight> flightList){
 		for(Flight flight : flightList){
 			flight.setSeatsLeft(flight.getSeatsLeft()-1);
 		}
 		return null;
 	}
}
