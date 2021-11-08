package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
