package com.example

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import groovy.time.TimeCategory
import groovy.util.slurpersupport.GPathResult
import org.springframework.boot.jackson.JsonComponent
import org.springframework.boot.jackson.JsonObjectSerializer

/**
 * This class converts the parsed flight (XML) data to the expected JSON format.
 */
@JsonComponent
class FlightsJsonSerializer extends JsonObjectSerializer<GPathResult> {

    private final static CURRENCY_CODE_LENGTH = 3

    private final static TIME_FORMAT = 'HH:mma'

    private final static DATE_FORMAT = 'dd-MM-yyyy'

    private final static ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

    @Override
    protected void serializeObject(GPathResult parsedFlights, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {

        List<Map> jsonFlights = parsedFlights.Flight.collect { flight ->
            getFlightProperties(flight)
        }

        jsonGenerator.writeObjectField("availability", [
                flight: jsonFlights
        ])
    }

    private Map getFareProperties(fares) {

        def parseAmount = { amountNode ->
            String amount = amountNode.text()

            [
                    currency: amount[0..<CURRENCY_CODE_LENGTH],
                    price   : amount[CURRENCY_CODE_LENGTH..-1].toBigDecimal()
            ]
        }

        def findFare = { fareClass ->
            def fare = fares.find { it.@class == fareClass }

            [
                    ticket    : parseAmount(fare.BasePrice),
                    bookingFee: parseAmount(fare.Fees),
                    tax       : parseAmount(fare.Tax)
            ]
        }

        [
                first   : findFare('FIF'),
                business: findFare('CIF'),
                economy : findFare('YIF')
        ]
    }

    private Date parseDateNode(dateNode) {
        Date.parse(ISO_DATE_FORMAT, dateNode.text())
    }

    private Map getFlightProperties(flight) {

        def temporalProperties = { Date date ->
            [date: date.format(DATE_FORMAT), time: date.format(TIME_FORMAT)]
        }

        Date departure = parseDateNode(flight.DepartureDate)
        Date arrival = parseDateNode(flight.ArrivalDate)

        String flightTime
        use(TimeCategory) {
            def duration = arrival - departure
            flightTime = "${duration.hours}:${duration.minutes}"
        }

        [
                operator    : flight.CarrierCode.text(),
                flightNumber: flight.FlightDesignator.text(),
                departsFrom : flight.OriginAirport.text(),
                arrivesAt   : flight.DestinationAirport.text(),
                departsOn   : temporalProperties(departure),
                arrivesOn   : temporalProperties(arrival),
                flightTime  : flightTime,
                farePrices  : getFareProperties(flight.Fares.Fare)
        ]
    }
}
