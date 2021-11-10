package com.cmpe275.AirlineReservationSystem.Util;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class BadRequest {
    private final int code;
    private final String message;
}