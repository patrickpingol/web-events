package com.thirio.controller

import com.thirio.model.Event
import com.thirio.service.DatabaseConnection
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author patrick.pingol
 */
@Controller
class WebController {
    private DatabaseConnection dbcon = new DatabaseConnection()

    @RequestMapping( value = ['', '/', '/index'] )
    String homePage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', 'EVENTS - ' + event.name )
            model.addAttribute( 'eventName', event.name )
            model.addAttribute( 'eventDate', event.date )
        } else {
            Event[] events = dbcon.getEventList( '', '' )
            model.addAttribute( 'title', 'EVENTS' )
            model.addAttribute( 'events', events )
        }

        return 'index'
    }

    @RequestMapping( value = '/event/create' )
    String createEventPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', 'EVENTS - ' + event.name )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'create-event'
    }

    @RequestMapping( value = '/lottery' )
    String lotteryPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', 'EVENTS - ' + event.name )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'lottery'
    }
}
