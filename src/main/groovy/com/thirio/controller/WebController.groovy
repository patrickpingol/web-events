package com.thirio.controller

import com.thirio.model.Event
import com.thirio.service.DatabaseConnection
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

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
            model.addAttribute( 'title', event.name.toUpperCase() )
            model.addAttribute( 'eventName', event.name.toUpperCase() )
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
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'create-event'
    }

    @RequestMapping( value = '/event/edit' )
    String editEventPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        Event[] events = dbcon.getEventList( '', '' )
        model.addAttribute( 'events', events )

        return 'edit-event'
    }

    @RequestMapping( value = '/event/status' )
    String editStatusPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        Event[] events = dbcon.getEventList( '', '' )
        model.addAttribute( 'events', events )

        return 'event-status'
    }

    @RequestMapping( value = '/import' )
    String csvUploadPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId,
                          @RequestParam( required = false, defaultValue = '' ) String message ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        if ( message.equalsIgnoreCase( 'success' ) )
            model.addAttribute( 'upload', true )
        else if ( message.equalsIgnoreCase( 'error' ) )
            model.addAttribute( 'upload', false )

        return 'import-csv'
    }

    @RequestMapping( value = '/attendance' )
    String attendancePage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId,
                           @RequestParam( required = false, defaultValue = '' ) String message ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'attendance'
    }

    @RequestMapping( value = '/student/register' )
    String createStudentPage( Model model,
                              @CookieValue( value = 'EVENTID', defaultValue = '' )
                                      String eventId,
                              @RequestParam( required = false, defaultValue = '' )
                                      String message
    ) {
        if ( message.equalsIgnoreCase( 'success' ) )
            model.addAttribute( 'create', true )
        else if ( message.equalsIgnoreCase( 'error' ) )
            model.addAttribute( 'create', false )

        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'create-student'
    }

    @RequestMapping( value = '/student/search' )
    String searchStudentPage( Model model,
                              @CookieValue( value = 'EVENTID', defaultValue = '' )
                                      String eventId
    ) {

        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'search-student'
    }

    @RequestMapping( value = '/lottery' )
    String lotteryPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'lottery'
    }

    @RequestMapping( value = '/live/count' )
    String liveCountPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name.toUpperCase() )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'live-count'
    }
}
