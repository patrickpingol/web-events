package com.thirio.service

import au.com.bytecode.opencsv.CSVReader
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import com.thirio.exception.ThirioEventsException
import com.thirio.model.Event
import com.thirio.model.Student
import groovy.sql.Sql
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.web.multipart.MultipartFile

import java.sql.SQLException

/**
 * @author patrick.pingol
 */
class DatabaseConnection {
    //local - HOME
    /*private static final String HOST = 'localhost'
    private static final String CONN_USER = 'postgres'
    private static final String CONN_PASS = ''
    private static final String DB = 'db_thirio'
    private static final SCHEMA = 'events'
    private static final String CONN_URL = "jdbc:postgresql://$HOST:5432/$DB"
    private static final String CONN_DRIVER = 'org.postgresql.Driver'
    private static ObjectMapper mapper*/

    //local - toro
    private static final String HOST = 'localhost'
    private static final String CONN_USER = 'patrick.pingol'
    private static final String CONN_PASS = ''
    private static final String DB = 'patrick.pingol'
    private static final SCHEMA = 'events'
    private static final String CONN_URL = "jdbc:postgresql://$HOST:5432/$DB"
    private static final String CONN_DRIVER = 'org.postgresql.Driver'
    private static ObjectMapper mapper

    static {
        // configure jackson and enable it to use Jackson and JAXB annotations on our beans
        if ( mapper == null ) {
            mapper = new ObjectMapper()
            mapper.setAnnotationIntrospector( AnnotationIntrospector.pair(
                    new JacksonAnnotationIntrospector(),
                    new JaxbAnnotationIntrospector( TypeFactory.defaultInstance() ) )
            )
            mapper.enable( SerializationFeature.INDENT_OUTPUT )
        }
    }

    static Sql connectSql() {
        try {
            Sql.newInstance( CONN_URL, CONN_USER, CONN_PASS, CONN_DRIVER )
        } catch ( SQLException e ) {
            throw new ThirioEventsException( "Failed to connect to DB:\n${e.message}" )
        }
    }

    //Event methods
    static Integer createEvent( Event event ) {
        Sql conn = connectSql()
        try {
            String query = "INSERT INTO ${SCHEMA}.tbl_events(name, date) VALUES (:name, :date)"
            def req = conn.executeInsert( query, [name: event.name, date: event.date] )
            conn.close()

            Integer.parseInt( req[0][0].toString() )
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Event[] getEventList( String name, String date ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${SCHEMA}.tbl_events"
            def req
            if ( name != '' || date != '' ) {
                query += ' WHERE'
                Boolean firstInsert = true
                Map params = [:]
                if ( name != '' ) {
                    params.put( 'name', '%' + name + '%' )
                    query += ' name=:name'
                    firstInsert = false
                }

                if ( date != '' ) {
                    params.put( 'date', date )
                    query += firstInsert ? ' date=:date' : ' AND date=:date'
                }
                req = conn.rows( query, params )
            } else {
                req = conn.rows( query )
            }
            conn.close()
            Event[] eventList = mapper.readValue( mapper.writeValueAsString( req ), Event[] )

            eventList
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Event getEvent( Integer id ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${SCHEMA}.tbl_events WHERE id=:id"
            def req = conn.firstRow( query, [id: id] )
            conn.close()

            Event eventFromDb = mapper.readValue(
                    mapper.writeValueAsString( req ), Event.class
            )

            eventFromDb
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Boolean deleteEvent( Integer id ) {
        Sql conn = connectSql()
        try {
            String query = "DELETE FROM ${SCHEMA}.tbl_events WHERE id=:id"
            def req = conn.execute( query, [id: id] )
            conn.close()

            return !req
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static String createStudent( Student student ) {
        Sql conn = connectSql()
        try {
            String query = "INSERT INTO ${SCHEMA}.tbl_students(id, lastname, firstname, college, course) VALUES " +
                    "(:id, :lastName, :firstName, :college, :course) " +
                    "ON CONFLICT DO NOTHING"
            def params = [id       : student.id,
                          lastName : student.lastName,
                          firstName: student.firstName,
                          college  : student.college,
                          course   : student.course]
            def req = conn.executeInsert( query, params )
            conn.close()

            req[0][0].toString()
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Stduent methods
    static Boolean createStudents( MultipartFile file ) {
        //TODO: Populate tbl_students using CSV file
        Sql conn = connectSql()
        try {
            File newFile = new File( '/tmp/' + file.getOriginalFilename() )
            file.transferTo( newFile )
            CSVReader reader = new CSVReader( new FileReader( newFile ) )
            List<String[]> allRows = reader.readAll()

            String query = "INSERT INTO ${SCHEMA}.tbl_students(id, lastname, firstname, college, course) VALUES " +
                    "(?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"

            conn.withBatch( allRows.size(), query ) { ps ->
                allRows.each { str ->
                    ps.addBatch( str[0], str[1], str[2], str[3], str[4] )
                }
            }

            true
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student[] getStudentList( String lastName, String firstName, String college, String course ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${SCHEMA}.tbl_students"
            def req
            if ( lastName != '' || firstName != '' || college != '' || course != '' ) {
                Map<String, String> params = [:]
                Boolean firstInsert = true
                query += ' WHERE'
                if ( lastName != '' ) {
                    params.put( 'lastName', '%' + lastName + '%' )
                    query += " lastname LIKE :lastName"
                    firstInsert = false
                }

                if ( firstName != '' ) {
                    params.put( 'firstName', '%' + firstName + '%' )
                    query += firstInsert ? " firstname LIKE :firstName" : " AND firstname LIKE :firstName"
                    firstInsert = false
                }

                if ( college != '' ) {
                    params.put( 'college', college )
                    query += firstInsert ? " college=:college" : " AND college=:college"
                    firstInsert = false
                }

                if ( course != '' ) {
                    params.put( 'course', course )
                    query += firstInsert ? " course=:course" : " AND course=:course"
                }
                req = conn.rows( query, params )
            } else {
                req = conn.rows( query )
            }
            conn.close()
            Student[] students = mapper.readValue( mapper.writeValueAsString( req ), Student[] )

            students
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student getStudentById( String id ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${SCHEMA}.tbl_students WHERE id=:id"
            def req = conn.firstRow( query, [id: id] )
            conn.close()

            Student student = mapper.readValue( mapper.writeValueAsString( req ), Student.class )

            student
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Boolean deleteStudent( String id ) {
        Sql conn = connectSql()
        try {
            String query = "DELETE FROM ${SCHEMA}.tbl_students WHERE id=:id"
            def req = conn.execute( query, [id: id] )
            conn.close()

            return !req
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Register methods
    static Student registerStudent( String studentId, Integer eventId ) {
        Sql conn = connectSql()
        try {
            Student student = getStudentById( studentId )

            if ( student.id != null ) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern( 'yyyy-MM-dd hh:mm:ssaaa' )
                DateTime dateInPH = DateTime.now( DateTimeZone.forID( 'Asia/Manila' ) )
                String dateStr = formatter.print( dateInPH ).toLowerCase()
                String query = "SELECT status, time FROM ${SCHEMA}.tbl_register WHERE " +
                        "eventid=:eventId AND studentid=:studentId ORDER BY time DESC OFFSET 0 LIMIT 1"
                def req = conn.firstRow( query, [eventId: eventId, studentId: studentId] )
                if ( req == null )
                    student.status = 'IN'
                else {
                    String status = req['status']
                    if ( status == 'IN' )
                        student.status = 'OUT'
                    else
                        student.status = 'IN'
                }

                query = "INSERT INTO ${SCHEMA}.tbl_register (eventid, studentid, status, time) VALUES " +
                        "(:eventId, :studentId, :status, :time)"
                def params = [
                        eventId  : eventId,
                        studentId: studentId,
                        status   : student.status,
                        time     : dateStr
                ]
                if ( !conn.execute( query, params ) )
                    return student
            } else {
                throw new ThirioEventsException( 'Invalid AUF Student ID.' )
            }

            throw new ThirioEventsException( 'Something went extremely wrong here.' )
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student insertToLotteryTable( Integer eventId ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${SCHEMA}.tbl_students WHERE id = " +
                    "(SELECT DISTINCT(studentid) FROM " +
                    "(SELECT * FROM ${SCHEMA}.tbl_register WHERE eventid=:eventId AND studentid NOT IN " +
                    "(SELECT studentid FROM ${SCHEMA}.tbl_lottery WHERE eventid=:eventId) ORDER BY random() " +
                    "OFFSET 0 LIMIT 1) AS b)"
            Student student
            def req = conn.firstRow( query, [eventId: eventId] )
            if ( req != null ) {
                student = mapper.readValue(
                        mapper.writeValueAsString( req ),
                        Student.class
                )
                if ( !conn.execute(
                        "INSERT INTO ${SCHEMA}.tbl_lottery(studentid, eventid) VALUES (:studentId, :eventId)",
                        [studentId: student.id, eventId: eventId]
                ) )
                    return student
            } else {
                throw new ThirioEventsException( 'All attendees have been drawn.' )
            }
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }
}
