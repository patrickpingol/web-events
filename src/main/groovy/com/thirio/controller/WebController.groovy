package com.thirio.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author patrick.pingol
 */
@Controller
class WebController {

    @RequestMapping( value = ['', '/', '/index'] )
    String homePage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {


        return 'index'
    }

    @RequestMapping( value = '/lottery' )
    String lotteryPage( Model model, @CookieValue( value = 'EVENTID', defaultValue = '' ) String eventId ) {
        /*if ( eventId == '' )
            return 'Please select an event first'*/
        return 'lottery'
    }
}
