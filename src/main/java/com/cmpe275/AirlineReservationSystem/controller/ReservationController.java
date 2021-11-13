package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.Util.BadRequest;
import com.cmpe275.AirlineReservationSystem.Util.ExceptionHandle;
import com.cmpe275.AirlineReservationSystem.Util.Response;
import com.cmpe275.AirlineReservationSystem.service.ReservationService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

/**
 * 
 * @author payalghule
 * Reservation controller to handle all reservation service method
 *
 */
@Transactional(rollbackOn = {IOException.class, SQLException.class})
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Get the reservation
     * @param number
     * @param xml
     * @return
     */
    @RequestMapping(value="/reservation/{number}", method= RequestMethod.GET, produces={"application/json", "application/xml"})
    public ResponseEntity<?> getReservation(
            @PathVariable String number,
            @RequestParam(value="xml", required = false)       String xml
    ) {
    	try {
    		return reservationService.getReservation(number);
    	}catch (NotFoundException e) {
    		return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
         
    }
    
    /**
     * create the reservation
     * @param passengerId
     * @param flightNumbers
     * @param xml
     * @return
     */
    @RequestMapping(value="/reservation", method=RequestMethod.POST, produces={"application/json", "application/xml"})
 	public ResponseEntity<?> createReservation(
 			@RequestParam("passengerId") String passengerId,
 			@RequestParam("flightNumbers") List<String> flightNumbers,
 			@RequestParam(value = "xml", required=false) String xml
 			)
 			 {
 		
    	try {
			return reservationService.createReservation(passengerId, flightNumbers);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}
 		
 	}
    
    /**
     * update the reservation
     * @param number
     * @param flightsAdded
     * @param flightsRemoved
     * @param xml
     * @return
     */
    @RequestMapping(value="/reservation/{number}", method=RequestMethod.POST, produces={"application/json", "application/xml"})
   	public ResponseEntity<?> updateReservation(
   			@PathVariable String number,
   			@RequestParam(required=false) List<String> flightsAdded,
   			@RequestParam(required=false) List<String> flightsRemoved,
   			@RequestParam(value = "xml", required=false) String xml
   			)
   			 {
    	try {
			return reservationService.updateReservation(number, flightsAdded,flightsRemoved);
    	}catch (IllegalArgumentException e) {
    		return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(400, e.getMessage())));
		}catch (NotFoundException e) {
			return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
   	}
    
    /**
     * cancel the reservation
     * @param number
     * @param xml
     * @return
     */
    @RequestMapping(value="/reservation/{number}", method=RequestMethod.DELETE, produces={"application/json", "application/xml"})
   	public ResponseEntity<?> cancelReservation(
   			@PathVariable String number,
   			@RequestParam(value = "xml", required=false) String xml
   			)
   			 {
    	try {
    		reservationService.cancelReservation(number);
    		return ResponseEntity.status(HttpStatus.OK).body(new Response(200,"Reservation with number " + number + " is cancelled successfully "));
    	}catch (NotFoundException e) {
    		return ResponseEntity.badRequest().body(new ExceptionHandle(new BadRequest(404, e.getMessage())));
		}
	
   	}
}
