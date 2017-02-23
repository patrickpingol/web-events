package com.thirio.controller

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
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
    static ResponseEntity getEvents() {
        errorCheck() {
            dbcon.getEventList()
        }
    }

    @RequestMapping( value = '/event/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getEventById(
            @PathVariable
                    Integer id
    ) {
        errorCheck() {
            dbcon.getEvent( id )
        }
    }

    @RequestMapping( value = '/student/list/create', method = [RequestMethod.POST] )
    static ResponseEntity createStudentList(
            @RequestBody
                    MultipartFile csvFile
    ) {
        //TODO: Propagate tbl_students using CSV File
        new ResponseEntity( HttpStatus.OK )
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
