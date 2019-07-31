package com.example

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController
class FlightController {

    private final FlightApiClient flightApiClient

    FlightController(FlightApiClient flightApiClient) {
        this.flightApiClient = flightApiClient
    }

    /**
     * Endpoint for retrieving flights. The following optional request parameters may be passed
     * <ul>
     *     <li>origin</li>
     *     <li>destination</li>
     *     <li>start</li>
     *     <li>end</li>
     *     <li>pax</li>
     * </ul>
     * @param flightCommand
     * @return
     */
    @GetMapping("/")
    def list(@Valid FlightCommand flightCommand) {
        flightApiClient.getFlights(flightCommand)
    }
}





