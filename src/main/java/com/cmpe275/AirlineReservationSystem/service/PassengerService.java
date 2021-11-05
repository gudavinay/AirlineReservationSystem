package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PassengerService {
    @Autowired
    private PassengerRepository passengerRepository;

    public Passenger createPassenger(Passenger passenger){
        return passengerRepository.save(passenger);
    }

    public ResponseEntity<?> updatePassenger(String id,
                                          String firstname,
                                          String lastname,
                                          String age,
                                          String gender,
                                          String phone){
        Optional<Passenger> existingPass=passengerRepository.findById(id);
        if(existingPass.isPresent()){
            Passenger passenger=existingPass.get();
            passenger.setAge(Integer.parseInt(age));
            passenger.setLastname(lastname);
            passenger.setFirstname(firstname);
            passenger.setGender(gender);
            passenger.setPhone(phone);
            Passenger res=passengerRepository.save(passenger);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger Does not exist");
        }
        //return new ResponseEntity<>(, HttpStatus.OK);
    }

    public boolean deletePassenger(String id){
         passengerRepository.deleteById(id);
         return true;
    }
}
