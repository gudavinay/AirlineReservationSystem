package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.transaction.Transactional;

@Transactional
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value="/reservation/{number}", method= RequestMethod.GET, produces={"application/json", "application/xml"})
    public ResponseEntity<?> getReservation(
            @PathVariable String number,
            @RequestParam(value="xml", required = false)       String xml
    ) {
        return reservationService.getReservation(number);
    }
    
    
    @RequestMapping(value="/reservation", method=RequestMethod.POST, produces={"application/json", "application/xml"})
 	public ResponseEntity<?> createReservation(
 			@RequestParam("passengerId") String passengerId,
 			@RequestParam("flightNumbers") List<String> flightNumbers,
 			@RequestParam(value = "xml", required=false) String xml
 			)
 			 {
 		
 		 ResponseEntity<?> res= reservationService.createReservation(passengerId, 
 				flightNumbers);		 
 		 return res;
 	}
    
    @RequestMapping(value="/reservation/{number}", method=RequestMethod.POST, produces={"application/json", "application/xml"})
   	public ResponseEntity<?> updateReservation(
   			@PathVariable String number,
   			@RequestParam("flightsAdded") List<String> flightsAdded,
   			@RequestParam("flightsRemoved") List<String> flightsRemoved,
   			@RequestParam(value = "xml", required=false) String xml
   			)
   			 {
   		
   		 ResponseEntity<?> res= reservationService.updateReservation(number, 
   				flightsAdded,flightsRemoved);		 
   		 return res;
   	}
    
    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE, produces={"application/json", "application/xml"})
   	public ResponseEntity<?> cancelReservation(
   			@PathVariable String number,
   			@RequestParam(value = "xml", required=false) String xml
   			)
   			 {
   		
   		 ResponseEntity<?> res= reservationService.cancelReservation(number);		 
   		 return res;
   	}
}
