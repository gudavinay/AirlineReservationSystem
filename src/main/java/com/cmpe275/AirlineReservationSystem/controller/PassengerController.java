package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.awt.*;

@Transactional
@RestController
public class PassengerController {

    @Autowired
    private PassengerService service;

    @RequestMapping(value="/passenger/{id}", method=RequestMethod.PUT, produces={"application/json", "application/xml"})
    public ResponseEntity<?> updatePassenger(
            @PathVariable String id,
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname")  String lastname,
            @RequestParam("age")       String age,
            @RequestParam("gender")    String gender,
            @RequestParam("phone")     String phone,
            @RequestParam(value="xml", required = false)       String xml
    ) {
        return service.updatePassenger(id, firstname,
                lastname, age, gender, phone);
    }

}
