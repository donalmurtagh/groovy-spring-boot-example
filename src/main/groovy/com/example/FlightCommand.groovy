package com.example

import org.springframework.format.annotation.DateTimeFormat

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

class FlightCommand {

    @NotBlank
    String origin = "DUB"

    @NotBlank
    String destination = "LHR"

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date start = new Date()

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date end = start + 1

    @Min(1L)
    int pax = 1
}
