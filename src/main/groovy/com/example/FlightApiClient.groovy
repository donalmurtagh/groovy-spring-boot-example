package com.example

import groovy.util.slurpersupport.GPathResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class FlightApiClient {

    private static final API_DATE_FORMAT = 'yyyy-MM-dd'

    GPathResult getFlights(FlightCommand flightCommand) {

        String apiUrl = getRequestUrl(flightCommand)
        ResponseEntity<String> response = new RestTemplate().getForEntity(apiUrl, String)

        if (response.statusCode != HttpStatus.OK) {
            throw new FlightApiException("Failed flight API request: $apiUrl")
        }
        new XmlSlurper().parseText(response.body)
    }

    /**
     * Create a request URL for the flights web service, e.g.
     * https://private-anon-9ef5ee0a1d-mockairline.apiary-mock.com/flights/DUB/LHR/2019-10-10/2019-01-01/1
     * @param flightCommand
     * @return
     */
    private String getRequestUrl(FlightCommand flightCommand) {

        String urlParams = flightCommand.with {
            [
                    origin,
                    destination,
                    start.format(API_DATE_FORMAT),
                    end.format(API_DATE_FORMAT),
                    pax
            ].join('/')
        }

        "https://private-anon-9ef5ee0a1d-mockairline.apiary-mock.com/flights/$urlParams"
    }
}
