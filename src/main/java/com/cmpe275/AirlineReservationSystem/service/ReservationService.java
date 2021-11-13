package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 
 * @author payalghule
 *
 */
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
	@Autowired
	private PassengerRepository passengerRepository;
	
	@Autowired
	private FlightRepository flightRepository;

	/**
	 * This method is used to get reservation for given id
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
    public ResponseEntity<?> getReservation(String id) throws NotFoundException {
        Optional<Reservation> existingRes=reservationRepository.findById(id);
		//ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation with number "+id+" does not exist");
		if(existingRes.isPresent()){
            Reservation reservation = existingRes.get();
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }else throw new NotFoundException("Reservation with number " + id + " does not exist");
    }
    
    /**
     * This method is used to create a reservation for passenger
     * @param passengerId
     * @param flightNumbers
     * @return
     */
   public ResponseEntity<?> createReservation(String passengerId, List<String> flightNumbers) {
    	Optional<Passenger> passenger = passengerRepository.findById(passengerId);
    	
    	if(passenger.isPresent() && !CollectionUtils.isEmpty(flightNumbers)) {
    		List<String> trimmedFlightNumbers = flightNumbers.stream()
    		        .map(String::trim)
    		        .collect(Collectors.toList());
    		System.out.println("flight numbers: " + trimmedFlightNumbers);
    		List<Flight> flightList = getFlightList(trimmedFlightNumbers);
    		if(flightList.size()>1) {
    			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation(flightList);
        		if(isFirstTimeOverlap) {
        	    	System.out.println("Time overlap within same reservation");
        			throw new IllegalArgumentException("Can not create reservation,there is time overlap between the flights within same reservation");
        		}
        		boolean isSecondTimeOverlap=isTimeOverlapForSamePerson( passengerId,flightList);
        		if(isSecondTimeOverlap) {
        			System.out.println("Time overlap within for same Person");
        			throw new IllegalArgumentException("Can not create reservation,the same person cannot have two reservations that overlap with each other");
        		}
    		}
    		//check for capacity
    		
    		if(isSeatsAvailable(flightList)) {
    			//calculate fare
    			int fare=calculatePrice(flightList);
    			
    			//make new reservation
    			Reservation newReservation= new Reservation( flightList.get(0).getOrigin(), flightList.get(flightList.size()-1).getDestination(), fare, passenger.get(),flightList);
    			
    			//add to the passenger
    			passenger.get().getReservations().add(newReservation);
    			//add to the passenger to flight
    			for(Flight flight : flightList){
    				flight.getPassengers().add(passenger.get());
    			}
    			reduceAvailableFlightSeats(flightList);
    			Reservation res =reservationRepository.save(newReservation);
    			return new ResponseEntity<>(res, HttpStatus.OK);
    		}else {
    	    	throw new IllegalArgumentException("Can not create reservation, No seats available. Flight capacity full.");
    		}
	
    	}else  throw new IllegalArgumentException( "Can not create reservation, Passenger or flight Number must be empty.");
	
    }
   
 /**
  * This method is used to update a reservation for passenger
  * @param number
  * @param flightsAdded
  * @param flightsRemoved
  * @return
  * @throws NotFoundException
  */
    
    public ResponseEntity<?> updateReservation( String number, List<String> flightsAdded, List<String>  flightsRemoved) throws NotFoundException {
    	Reservation existingReservation= reservationRepository.findByReservationNumber(number); 
    	if(existingReservation!=null) {
    		List<Flight> existingFlightList = existingReservation.getFlights();
    		if( CollectionUtils.isEmpty(flightsAdded) && !CollectionUtils.isEmpty(flightsRemoved) && existingFlightList.size()<= flightsRemoved.size()) {
    			throw new IllegalArgumentException("Can not update, Reservation has lesser or equal flights user trying to remove");
    		}
    		Passenger passenger= existingReservation.getPassenger();
        	String passengerId= passenger.getId();
        	
        	int existingPrice = existingReservation.getPrice();
        	//Removing flights first
        	if(!CollectionUtils.isEmpty(flightsRemoved)) {
        		System.out.println("flightsRemoved: " +flightsRemoved);
        		List<String> trimmedFlightsRemoved = flightsRemoved.stream()
        		        .map(String::trim)
        		        .collect(Collectors.toList());
    			List<Flight> flightListToRemove = getFlightList(trimmedFlightsRemoved);
    			if(existingFlightList.size()!=0) {
    				existingFlightList.removeAll(flightListToRemove);
    				increaseAvailableFlightSeats(flightListToRemove);	
    			}
    			int newPrice=existingPrice-calculatePrice(flightListToRemove);
    			if(existingFlightList.size()>0) {
    				existingReservation.setOrigin(existingFlightList.get(0).getOrigin());
        			existingReservation.setDestination(existingFlightList.get(existingFlightList.size()-1).getDestination());
    			}
    			existingReservation.setPrice(newPrice);	
    		}
        	////Adding flights later
    		if(!CollectionUtils.isEmpty(flightsAdded)) {
    			System.out.println("flightsAdded: " +flightsAdded);
    			List<String> trimmedFlightsAdded = flightsAdded.stream()
        		        .map(String::trim)
        		        .collect(Collectors.toList());
    			List<Flight> flightListToAdd = getFlightList(trimmedFlightsAdded);
    			if(flightListToAdd.size()>1) {
        			boolean isFirstTimeOverlap=isTimeOverlapWithinReservation(flightListToAdd);
            		if(isFirstTimeOverlap) {
            	    	System.out.println("Time overlap within same reservation");
            			throw new IllegalArgumentException("Can not update, there is time overlap between the flights within same reservation");
            		}
            		boolean isSecondTimeOverlap=isTimeOverlapForSamePerson( passengerId,flightListToAdd);
            		if(isSecondTimeOverlap) {
            			System.out.println("Time overlap within for same Person");
            			throw new IllegalArgumentException("Can not update, the same person cannot have two reservations that overlap with each other");
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
    			}else {
    				throw new IllegalArgumentException("No seats available. Flight capacity full.");
    			}
    			
    		}
    		
    		//passenger.getReservation().add(existingReservation);
    		Reservation resUpdate =reservationRepository.save(existingReservation);
    		return new ResponseEntity<>(resUpdate, HttpStatus.OK);
    		
    	}else {
    		 throw new NotFoundException( "No reservation found for given reservation number");
    	}
    	
    }
    
    /**
     * This method is used to cancel reservation for passenger
     * @param reservationNumber
     * @return
     * @throws NotFoundException
     */
    public ResponseEntity<?> cancelReservation( String reservationNumber) throws NotFoundException {
    	Reservation res=reservationRepository.findByReservationNumber(reservationNumber);
        if(res !=null){
        	
        	res.getPassenger().getReservations().remove(res);
        	reservationRepository.delete(res);
        	List<Flight> flightList=res.getFlights();
        	if(flightList.size()!=0) 
        		increaseAvailableFlightSeats(flightList);
        	return new ResponseEntity<>("Reservation with number " + reservationNumber + " is canceled successfully ", HttpStatus.OK);
        }else {
            throw new NotFoundException("Reservation with number "+reservationNumber+" does not exist");
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
    	List<Reservation> reservationList = passenger.get().getReservations();
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
    
    public void reduceAvailableFlightSeats(List<Flight> flightList){
 		for(Flight flight : flightList){
 			flight.setSeatsLeft(flight.getSeatsLeft()-1);
 		}
 		
 	}
    
    public void increaseAvailableFlightSeats(List<Flight> flightList){
 		for(Flight flight : flightList){
 			flight.setSeatsLeft(flight.getSeatsLeft()+1);
 		}
 	}
    

}
