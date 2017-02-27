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
            model.addAttribute( 'title', event.name + ' - EVENTS' )
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
            model.addAttribute( 'title', event.name + ' - EVENTS' )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'create-event'
    }

    @RequestMapping( value = '/event/edit' )
    String editEventPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name + ' - EVENTS' )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        Event[] events = dbcon.getEventList( '', '' )
        model.addAttribute( 'events', events )

        return 'edit-event'
    }

    @RequestMapping( value = '/import' )
    String csvUploadPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId,
                          @RequestParam( required = false, defaultValue = '' ) String message ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name + ' - EVENTS' )
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
            model.addAttribute( 'title', event.name + ' - EVENTS' )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'attendance'
    }

    @RequestMapping( value = '/lottery' )
    String lotteryPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        if ( eventId != '' ) {
            Event event = dbcon.getEvent( Integer.parseInt( eventId.toString() ) )
            model.addAttribute( 'title', event.name + ' - EVENTS' )
        } else {
            model.addAttribute( 'title', 'EVENTS' )
        }

        return 'lottery'
    }
}
