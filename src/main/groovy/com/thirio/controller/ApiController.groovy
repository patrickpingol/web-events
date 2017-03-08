package com.thirio.controller

import com.thirio.exception.ThirioEventsException
import com.thirio.model.Event
import com.thirio.model.Student
import com.thirio.service.EventService
import com.thirio.service.LotteryService
import com.thirio.service.RegisterService
import com.thirio.service.StudentService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
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
@CompileStatic
@RequestMapping('/api')
@SuppressWarnings("GroovyUnusedDeclaration")
class ApiController {

    @Autowired
    EventService eventService

    @Autowired
    StudentService studentService

    @Autowired
    RegisterService registerService

    @Autowired
    LotteryService lotteryService

    @RequestMapping( value = '/event/create', method = [RequestMethod.POST] )
    static ResponseEntity createEvent(@RequestBody Event event) {
        errorCheck {
            eventService.createEvent event
        }
    }

    @RequestMapping( value = '/event', method = [RequestMethod.GET] )
    static ResponseEntity getEvents(
            @RequestParam(required = false, defaultValue = '') String name,
            @RequestParam(required = false, defaultValue = null) Date date) {
        errorCheck {
            eventService.getEventList name, date
        }
    }

    @RequestMapping( value = '/event/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getEventById(
            @RequestParam(required = false, defaultValue = '') String status,
            @PathVariable Integer id) {
        errorCheck {
            if ( status == '' )
                eventService.getEvent id
            else
                eventService.getEventStatus id, status
        }
    }

    @RequestMapping( value = '/event/delete/{id}', method = [RequestMethod.DELETE] )
    static ResponseEntity deleteEventById(@PathVariable Integer id) {
        errorCheck {
            eventService.deleteEvent id
        }
    }

    @RequestMapping( value = '/student/create', method = [RequestMethod.POST] )
    static ResponseEntity createStudent(@RequestBody Student student) {
        errorCheck {
            studentService.createStudent student
        }
    }

    @RequestMapping( value = '/student', method = [RequestMethod.GET] )
    static ResponseEntity getStudentList(
            @RequestParam(required = false, defaultValue = '') String id,
            @RequestParam(required = false, defaultValue = '') String lastName,
            @RequestParam(required = false, defaultValue = '') String firstName,
            @RequestParam(required = false, defaultValue = '') String college,
            @RequestParam(required = false, defaultValue = '') String course) {
        errorCheck {
            studentService.getStudentList id, lastName, firstName, college, course
        }
    }

    @RequestMapping( value = '/student/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getStudent(@PathVariable String id) {
        errorCheck {
            studentService.getStudentById id
        }
    }

    @RequestMapping( value = '/student/delete/{id}', method = [RequestMethod.DELETE] )
    static ResponseEntity deleteStudent(@PathVariable String id) {
        errorCheck {
            studentService.deleteStudent id
        }
    }

    @RequestMapping( value = '/student/csv', method = [RequestMethod.POST] )
    static ResponseEntity uploadCsv(MultipartFile file) {
        errorCheck {
            studentService.createStudents file
        }
    }

    @RequestMapping( value = '/student/register', method = [RequestMethod.POST] )
    static ResponseEntity registerStudent(
            @RequestParam String studentId,
            @RequestParam(required = false, defaultValue = 'false') Boolean notEligible,
            @CookieValue(name = 'EVENTID', defaultValue = '') Integer eventId) {
        errorCheck {
            if (eventId == null)
                throw new ThirioEventsException( 'No event is selected' )

            registerService.registerStudent studentId, eventId, notEligible
        }
    }

    @RequestMapping( value = '/lottery/draw', method = [RequestMethod.GET] )
    static ResponseEntity drawStudent(@RequestParam Integer eventId) {
        errorCheck {
            lotteryService.insertToLotteryTable eventId
        }
    }

    @RequestMapping( value = '/live/{id}', method = [RequestMethod.GET] )
    static ResponseEntity getLiveCount(@PathVariable Integer id) {
        errorCheck {
            registerService.liveAttendeesCount id
        }
    }

    private static ResponseEntity errorCheck(Closure closure) {
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
