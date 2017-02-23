package com.thirio.service

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

import java.sql.SQLException

/**
 * @author patrick.pingol
 */
class DatabaseConnection {
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
                    query += firstInsert ? ' name=:name' : ' AND name=:name'
                    params.put( 'name', name )
                    firstInsert = false
                }

                if ( date != '' ) {
                    query += firstInsert ? ' date=:date' : ' AND date=:date'
                    params.put( 'date', date )
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

    static String createStudent( Student student ) {
        Sql conn = connectSql()
        try {
            String query = "INSERT INTO ${SCHEMA}.tbl_students(id, lastname, firstname, college, course) VALUES " +
                    "(:id, :lastName, :firstName, :college, :course)"
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
                    params.put( 'lastName', lastName )
                    query += firstInsert ? " lastname=:lastName" : " AND lastname=:lastName"
                    firstInsert = false
                }

                if ( firstName != '' ) {
                    params.put( 'firstName', firstName )
                    query += firstInsert ? " firstname=:firstName" : " AND firstname=:firstName"
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
}
