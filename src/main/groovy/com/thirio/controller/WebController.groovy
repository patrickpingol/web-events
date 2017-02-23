package com.thirio.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author patrick.pingol
 */
@Controller
class WebController {

    @RequestMapping( value = ['', '/'], method = [RequestMethod.GET] )
    String homePage( Model model, @CookieValue(value = 'EVENTID', defaultValue = '') String eventId ) {


        return 'index'
    }
}
