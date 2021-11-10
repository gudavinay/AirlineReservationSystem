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
    			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation(flightList);
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
    			//calculate fare
    			int fare=calculatePrice(flightList);
    			
    			//make new reservation
    			Reservation newReservation= new Reservation( flightList.get(0).getOrigin(), flightList.get(flightList.size()-1).getDestination(), fare, passenger.get(),flightList);
    			
    			//add to the passenger
    			passenger.get().getReservation().add(newReservation);
    			//add to the passenger to flight
    			for(Flight flight : flightList){
    				flight.getPassengers().add(passenger.get());
    			}
    			reduceAvailableFlightSeats(flightList);
    			Reservation res =reservationRepository.save(newReservation);
    			return new ResponseEntity<>(res, HttpStatus.OK);
    		}
	
    	}
    	
    	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request");
	
    }
   
 
    
    public ResponseEntity<?> updateReservation( String number, List<String> flightsAdded, List<String>  flightsRemoved) {
    	Reservation existingReservation= reservationRepository.findByReservationNumber(number);
    	Passenger passenger= existingReservation.getPassenger();
    	String passengerId= passenger.getId();
    	List<Flight> existingFlightList = existingReservation.getFlights();
    	int existingPrice = existingReservation.getPrice();
    	if(existingReservation!=null) {
    		if(!CollectionUtils.isEmpty(flightsAdded)) {
    			List<Flight> flightListToAdd = getFlightList(flightsAdded);
    			if(flightListToAdd.size()>1) {
        			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation(flightListToAdd);
            		if(isFirstTimeOverlap) {
            	    	System.out.println("Time overlap within same reservation");
            			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not update ! There is time overlap between the flights within same reservation");
            		}
            		boolean isSecondTimeOverlap=isTimeOverlapForSamePerson( passengerId,flightListToAdd);
            		if(isSecondTimeOverlap) {
            			System.out.println("Time overlap within for same Person");
            			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not update! The same person cannot have two reservations that overlap with each other");
            		}
        		}
    			if(isSeatsAvailable(flightListToAdd)) {
    			int newPrice=existingPrice+calculatePrice(flightListToAdd);
    			existingFlightList.addAll(flightListToAdd);
    			existingReservation.setFlights(existingFlightList);
    			existingReservation.setOrigin(existingFlightList.get(0).getOrigin());
    			existingReservation.setDestination(existingFlightList.get(existingFlightList.size()-1).getDestination());
    			existingReservation.setPrice(newPrice);
    			reduceAvailableFlightSeats(flightListToAdd);
    			}
    			
    		}
    		if(!CollectionUtils.isEmpty(flightsRemoved)) {
    			int newRemovalPrice=0;
    			List<Flight> flightListToRemove = getFlightList(flightsRemoved);
    			if(existingFlightList.size()!=0) {
    				existingFlightList.removeAll(flightListToRemove);
    				increaseAvailableFlightSeats(flightListToRemove);
//    				for(Flight existingFlight: existingFlightList) {
//    					for(Flight removeFlight : flightListToRemove) {
//    						if(existingFlight.getFlightNumber().equals(removeFlight.getFlightNumber())) {
//    							newRemovalPrice=existingPrice-removeFlight.getPrice();
//    							existingFlightList.remove(removeFlight);
//    							flightRepository.deleteByFlightNumber(existingFlight.getFlightNumber());
//    							
//    						}
//    					}
//    				}
    				
    				
    			}
    			int newPrice=existingPrice-calculatePrice(flightListToRemove);
    			existingReservation.setOrigin(existingFlightList.get(0).getOrigin());
    			existingReservation.setDestination(existingFlightList.get(existingFlightList.size()-1).getDestination());
    			existingReservation.setPrice(newPrice);
    				
    			
    		}
    		//passenger.getReservation().add(existingReservation);
    		Reservation resUpdate =reservationRepository.save(existingReservation);
    		return new ResponseEntity<>(resUpdate, HttpStatus.OK);
    		
    	}
    	
    	 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No reservation found for given reservation number");
    	
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
    
    private  boolean isTimeOverlapWithinReservation( List<Flight> flightList ) {
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
    		Optional<Flight> flight=flightRepository.getFlightByFlightNumber(flightNumber);
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
    
    public Flight increaseAvailableFlightSeats(List<Flight> flightList){
 		for(Flight flight : flightList){
 			flight.setSeatsLeft(flight.getSeatsLeft()+1);
 		}
 		return null;
 	}
    
    public boolean checkFlightSequnce(List<Flight> flightList) {
    	for(Flight flight : flightList){
 			
 		}
    	return false;
    }
}
