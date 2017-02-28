package com.thirio.controller

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import com.thirio.exception.ThirioEventsException
import com.thirio.model.Event
import com.thirio.model.Student
import com.thirio.service.DatabaseConnection
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.multipart.MultipartFile

/**
 * @author patrick.pingol
 */
@RestController
@RequestScope
@RequestMapping( '/api' )
class ApiController {
    private static DatabaseConnection dbcon = new DatabaseConnection()
    private static ObjectMapper mapper

    static {
        // configure jackson and enable it to use Jackson and JAXB annotations on our beans
        if ( mapper == null ) {
            mapper = new ObjectMapper()
            mapper.setAnnotationIntrospector( AnnotationIntrospector.pair(
                    new JacksonAnnotationIntrospector(),
                    new JaxbAnnotationIntrospector( TypeFactory.defaultInstance() ) )
            )
        }
    }

    @RequestMapping( value = '/event/create', method = [RequestMethod.POST] )
    static ResponseEntity createEvent(
            @RequestBody
                    Event event
    ) {
        errorCheck() {
            dbcon.createEvent( event )
        }
    }

    @RequestMapping( value = '/event', method = [RequestMethod.GET] )
    static ResponseEntity getEvents(
            @RequestParam( required = false, defaultValue = '' )
                    String name,
            @RequestParam( required = false, defaultValue = '' )
                    String date
    ) {
        errorCheck() {
            dbcon.getEventList( name, date )
        }
    }

    @RequestMapping( value = '/event/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getEventById(
            @RequestParam( required = false, defaultValue = '' )
                    String status,
            @PathVariable
                    Integer id
    ) {
        errorCheck() {
            if ( status == '' )
                dbcon.getEvent( id )
            else
                dbcon.getEventStatus( id, status )
        }
    }

    @RequestMapping( value = '/event/delete/{id}', method = [RequestMethod.DELETE] )
    static ResponseEntity deleteEventById(
            @PathVariable
                    Integer id
    ) {
        errorCheck() {
            dbcon.deleteEvent( id )
        }
    }

    @RequestMapping( value = '/student/create', method = [RequestMethod.POST] )
    static ResponseEntity createStudent(
            @RequestBody
                    Student student
    ) {
        errorCheck() {
            dbcon.createStudent( student )
        }
    }

    @RequestMapping( value = '/student', method = [RequestMethod.GET] )
    static ResponseEntity getStudentList(
            @RequestParam( required = false, defaultValue = '' )
                    String lastName,
            @RequestParam( required = false, defaultValue = '' )
                    String firstName,
            @RequestParam( required = false, defaultValue = '' )
                    String college,
            @RequestParam( required = false, defaultValue = '' )
                    String course
    ) {
        errorCheck() {
            dbcon.getStudentList( lastName, firstName, college, course )
        }
    }

    @RequestMapping( value = '/student/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getStudent(
            @PathVariable
                    String id
    ) {
        errorCheck() {
            dbcon.getStudentById( id )
        }
    }

    @RequestMapping( value = '/student/delete/{id}', method = [RequestMethod.DELETE] )
    static ResponseEntity deleteStudent(
            @PathVariable
                    String id
    ) {
        errorCheck() {
            dbcon.deleteStudent( id )
        }
    }

    @RequestMapping( value = '/student/csv', method = [RequestMethod.POST] )
    static ResponseEntity uploadCsv(
            MultipartFile file
    ) {
        errorCheck() {
            dbcon.createStudents( file )
        }
    }

    @RequestMapping( value = '/student/register', method = [RequestMethod.POST] )
    static ResponseEntity registerStudent(
            @RequestParam
                    String studentId,
            @CookieValue( name = 'EVENTID', defaultValue = '' )
                    String eventId
    ) {
        errorCheck() {
            if ( eventId == '' )
                throw new ThirioEventsException( 'No event is selected' )

            dbcon.registerStudent( studentId, Integer.parseInt( eventId ) )
        }
    }

    @RequestMapping( value = '/lottery/draw', method = [RequestMethod.GET] )
    static ResponseEntity drawStudent(
            @RequestParam
                    String eventId
    ) {
        errorCheck() {
            dbcon.insertToLotteryTable( Integer.parseInt( eventId ) )
        }
    }

    private static ResponseEntity errorCheck( def closure ) {
        try {
            def resp = closure()
            return new ResponseEntity(
                    [result: 'OK', message: resp], HttpStatus.OK
            )
        } catch ( Exception e ) {
            return new ResponseEntity(
                    [result: 'ERROR', message: e.message], HttpStatus.BAD_REQUEST
            )
        }
    }
}
