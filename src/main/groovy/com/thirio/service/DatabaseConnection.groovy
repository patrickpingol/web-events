package com.thirio.service

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import com.fasterxml.jackson.core.type.TypeReference
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
    //local - toro
    /*private static String host = 'localhost'
    private static String user = 'patrick.pingol'
    private static String pass = ''
    private static String db = 'patrick.pingol'
    private static String schema = 'events'
    private static final String connUrl = "jdbc:postgresql://$host:5432/$db"
    private static final String CONN_DRIVER = 'org.postgresql.Driver'
    private static ObjectMapper mapper*/

    private static String schema = 'events'
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
            File connFile = new File( 'database.json' )
            Map<String, String> connInfo = [:]

            if ( connFile.exists() ) {
                connInfo = mapper.readValue( connFile, new TypeReference<Map<String, String>>() {} )
                schema = connInfo.schema
            } else {
                connInfo.put( 'host', 'localhost' )
                connInfo.put( 'user', 'postgres' )
                connInfo.put( 'pass', 'password' )
                connInfo.put( 'db', 'ereg' )
                connInfo.put( 'schema', 'events' )
                schema = 'events'

                mapper.writeValue( connFile, connInfo )
            }
            Sql.newInstance( "jdbc:postgresql://${connInfo.host}:5432/${connInfo.db}", connInfo.user, connInfo.pass,
                    'org.postgresql.Driver' )
        } catch ( SQLException e ) {
            throw new ThirioEventsException( "Failed to connect to db:\n${e.message}" )
        }
    }

    //Event methods
    static Integer createEvent( Event event ) {
        Sql conn = connectSql()
        try {
            String query = "INSERT INTO ${schema}.tbl_events(name, date) VALUES (:name, :date)"
            def req = conn.executeInsert( query, [name: event.name.toUpperCase(), date: event.date] )
            conn.close()

            Integer.parseInt( req[0][0].toString() )
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Event[] getEventList( String name, String date ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${schema}.tbl_events"
            def req
            if ( name != '' || date != '' ) {
                query += ' WHERE'
                Boolean firstInsert = true
                Map params = [:]
                if ( name != '' ) {
                    params.put( 'name', '%' + name + '%' )
                    query += ' LOWER(name)=LOWER(:name)'
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
            String query = "SELECT * FROM ${schema}.tbl_events WHERE id=:id"
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
            String query = "DELETE FROM ${schema}.tbl_events WHERE id=:id"
            def req = conn.execute( query, [id: id] )
            conn.close()

            return !req
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student[] getEventStatus( Integer id, String status ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM (" +
                    "SELECT DISTINCT ON (student.id) student.*, register.status " +
                    "FROM ${schema}.tbl_register register, ${schema}.tbl_students student " +
                    "WHERE student.id = register.studentid AND register.eventid = :eventId " +
                    "ORDER BY student.id, register.time DESC) as b " +
                    "WHERE status=:status ORDER BY lastname, firstname, id;"
            def req = conn.rows( query, [eventId: id, status: status] )
            conn.close()

            Student[] student = mapper.readValue( mapper.writeValueAsString( req ), Student[] )

            student
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Stduent methods
    static String createStudent( Student student ) {
        Sql conn = connectSql()
        try {
            String query = "INSERT INTO ${schema}.tbl_students(id, lastname, firstname, college, course) VALUES " +
                    "(:id, :lastName, :firstName, :college, :course) " +
                    "ON CONFLICT DO NOTHING"
            def params = [id       : student.id,
                          lastName : student.lastName.toUpperCase(),
                          firstName: student.firstName.toUpperCase(),
                          college  : student.college.toUpperCase(),
                          course   : student.course]
            def req = conn.executeInsert( query, params )
            conn.close()

            req[0][0].toString()
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Boolean createStudents( MultipartFile file ) {
        Sql conn = connectSql()
        try {
            File newFile = new File( System.getProperty( 'java.io.tmpdir' ) + file.getOriginalFilename() )
            file.transferTo( newFile )
            CSVReader reader = new CSVReader( new FileReader( newFile ) )
            List<String[]> allRows = reader.readAll()

            String query = "INSERT INTO ${schema}.tbl_students(id, lastname, firstname, college, course) VALUES " +
                    "(?, ?, ?, ?, ?) ON CONFLICT DO NOTHING"

            conn.withBatch( allRows.size(), query ) { ps ->
                allRows.each { str ->
                    ps.addBatch( str[0], str[1].toUpperCase(), str[2].toUpperCase(), str[3].toUpperCase(), str[4] )
                }
            }

            conn.close()

            true
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student[] getStudentList( String id, String lastName, String firstName, String college, String course ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${schema}.tbl_students"
            def req
            if ( id != '' || lastName != '' || firstName != '' || college != '' || course != '' ) {
                Map<String, String> params = [:]
                Boolean firstInsert = true
                query += ' WHERE'
                if ( id != '' ) {
                    params.put( 'id', '%' + id + '%' )
                    query += " LOWER(id) LIKE LOWER(:id)"
                    firstInsert = false
                }

                if ( lastName != '' ) {
                    params.put( 'lastName', '%' + lastName + '%' )
                    query += firstInsert ? " LOWER(lastname) LIKE LOWER(:lastName)" : " AND LOWER(lastname) LIKE LOWER(:lastName)"
                    firstInsert = false
                }

                if ( firstName != '' ) {
                    params.put( 'firstName', '%' + firstName + '%' )
                    query += firstInsert ? " LOWER(firstname) LIKE LOWER(:firstName)" : " AND LOWER(firstname) LIKE LOWER(:firstName)"
                    firstInsert = false
                }

                if ( college != '' ) {
                    params.put( 'college', college )
                    query += firstInsert ? " LOWER(college)=LOWER(:college)" : " AND LOWER(college)=LOWER(:college)"
                    firstInsert = false
                }

                if ( course != '' ) {
                    params.put( 'course', course )
                    query += firstInsert ? " LOWER(course)=LOWER(:course)" : " AND LOWER(course)=LOWER(:course)"
                }
                req = conn.rows( query, params )
            } else {
                req = conn.rows( query )
            }
            conn.close()
            Student[] students = mapper.readValue( mapper.writeValueAsString( req ), Student[] )
            students = students.toSorted { a, b ->
                a.lastName <=> b.lastName ?: a.firstName <=> b.firstName ?: a.id <=> b.id
            }

            students
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Student getStudentById( String id ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT * FROM ${schema}.tbl_students WHERE id=:id"
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
            String query = "DELETE FROM ${schema}.tbl_students WHERE id=:id"
            def req = conn.execute( query, [id: id] )
            conn.close()

            return !req
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Register methods
    static Student registerStudent( String studentId, Integer eventId, Boolean notEligible ) {
        Sql conn = connectSql()
        try {
            Student student = getStudentById( studentId )

            if ( student.id != null ) {
                DateTimeFormatter formatter = DateTimeFormat.forPattern( 'yyyy-MM-dd hh:mm:ssaaa' )
                DateTime dateInPH = DateTime.now( DateTimeZone.forID( 'Asia/Manila' ) )
                String dateStr = formatter.print( dateInPH ).toLowerCase()
                String query = "SELECT status, time FROM ${schema}.tbl_register WHERE " +
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

                query = "INSERT INTO ${schema}.tbl_register (eventid, studentid, status, time) VALUES " +
                        "(:eventId, :studentId, :status, :time)"
                def params = [
                        eventId  : eventId,
                        studentId: studentId,
                        status   : student.status,
                        time     : dateStr
                ]
                if ( !conn.execute( query, params ) ) {
                    if ( notEligible && student.status == 'IN' ) {
                        conn.execute( "INSERT INTO ${schema}.tbl_lottery (studentid, eventid) " +
                                "SELECT :studentId, :eventId " +
                                "WHERE NOT EXISTS (SELECT * FROM ${schema}.tbl_lottery " +
                                "WHERE studentid=:studentId AND eventid=:eventId)",
                                [studentId: studentId, eventId: eventId] )
                    } else {
                        conn.execute( "DELETE FROM ${schema}.tbl_lottery WHERE studentid=:studentId " +
                                "AND eventid=:eventId", [studentId: studentId, eventId: eventId] )
                    }
                    conn.close()
                    return student
                }
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
            String query = "SELECT * FROM ${schema}.tbl_students WHERE id = " +
                    "(SELECT DISTINCT(studentid) FROM " +
                    "(SELECT * FROM ${schema}.tbl_register WHERE eventid=:eventId AND studentid NOT IN " +
                    "(SELECT studentid FROM ${schema}.tbl_lottery WHERE eventid=:eventId) ORDER BY random() " +
                    "OFFSET 0 LIMIT 1) AS b)"
            Student student
            def req = conn.firstRow( query, [eventId: eventId] )
            if ( req != null ) {
                student = mapper.readValue(
                        mapper.writeValueAsString( req ),
                        Student.class
                )
                if ( !conn.execute(
                        "INSERT INTO ${schema}.tbl_lottery(studentid, eventid) VALUES (:studentId, :eventId)",
                        [studentId: student.id, eventId: eventId]
                ) ) {
                    conn.close()
                    return student
                }
            } else {
                throw new ThirioEventsException( 'All attendees have been drawn.' )
            }
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Added feature
    static Integer liveAttendeesCount( Integer id ) {
        Sql conn = connectSql()
        try {
            String query = "SELECT COUNT(*) FROM (" +
                    "SELECT DISTINCT ON (student.id) student.*, register.status " +
                    "FROM ${schema}.tbl_register register, ${schema}.tbl_students student " +
                    "WHERE student.id = register.studentid AND register.eventid = :eventId " +
                    "ORDER BY student.id, register.time DESC) as b " +
                    "WHERE status='IN';"
            def req = conn.firstRow( query, [eventId: id] )
            conn.close()

            Integer.parseInt( req[0].toString() )
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    //Database Controls
    static Boolean clearTable( String tableName ) {
        Sql conn = connectSql()
        try {
            String[] tables = ['tbl_students', 'tbl_register', 'tbl_lottery', 'tbl_events']
            if ( !tables.contains( tableName ) ) {
                throw new ThirioEventsException( 'Table does not exist.' )
            }

            String query = "TRUNCATE TABLE ${schema}.${tableName}"
            if ( tableName == 'tbl_events' )
                query += ' RESTART IDENTITY'
            conn.execute( query )
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static Boolean clearAllTable() {
        Sql conn = connectSql()
        try {
            conn.execute( "TRUNCATE TABLE ${schema}.tbl_students, " +
                    "${schema}.tbl_events, " +
                    "${schema}.tbl_lottery, " +
                    "${schema}.tbl_register" )

            conn.close()

            true
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    static void backupDatabase() {
        Sql conn = connectSql()
        try {
            String query = "SELECT id, name FROM ${schema}.tbl_events"
            def req = conn.rows( query )
            req.each {
                String path = "backup/${it.name}"
                File folder = new File( path )
                if ( !folder.exists() ) {
                    if ( folder.mkdirs() ) {
                        writeCsv( path, 'tbl_lottery', (String) it.name )
                        writeCsv( path, 'tbl_register', (String) it.name )
                    }
                } else {
                    writeCsv( path, 'tbl_lottery', (String) it.name )
                    writeCsv( path, 'tbl_register', (String) it.name )
                }
            }
            writeCsv( 'backup', 'tbl_students' )
            writeCsv( 'backup', 'tbl_events' )
            conn.close()
        } catch ( Exception e ) {
            throw new ThirioEventsException( e.message )
        }
    }

    private static void writeCsv( String path, String tableName, String eventName = '', String college = '' ) {
        Sql conn = connectSql()
        String query
        String csv = college != '' && tableName == 'tbl_students' ?
                "$path/${tableName}_${college}.csv" :
                "$path/${tableName}.csv"
        CSVWriter writer = new CSVWriter( new FileWriter( csv ) )
        List<String[]> data = new ArrayList<String[]>()

        if ( tableName == 'tbl_students' || tableName == 'tbl_events' ) {
            query = "SELECT * FROM ${schema}.${tableName}"
            query += college != '' && tableName == 'tbl_students' ?
                    " WHERE college='$college'" :
                    ''
        } else if ( tableName == 'tbl_lottery' )
            query = "SELECT event.name, student.* " +
                    "FROM ${schema}.tbl_lottery lottery " +
                    "INNER JOIN ${schema}.tbl_students student ON student.id = lottery.studentid " +
                    "INNER JOIN ${schema}.tbl_events event ON event.id = lottery.eventid " +
                    "WHERE lottery.eventid = (SELECT id FROM ${schema}.tbl_events WHERE name='$eventName');"
        else if ( tableName == 'tbl_register' )
            query = "SELECT * FROM (SELECT DISTINCT ON (student.id) student.*, register.status " +
                    "FROM ${schema}.tbl_register register, ${schema}.tbl_students student " +
                    "WHERE student.id = register.studentid AND register.eventid = " +
                    "(SELECT id FROM ${schema}.tbl_events WHERE name='$eventName') " +
                    "ORDER BY student.id, register.time DESC) as b " +
                    "ORDER BY lastname, firstname, id;"

        conn.rows( query ).each {
            String[] strArray = new String[it.values().size()]
            int i = 0
            it.values().each {
                strArray[i] = String.valueOf( it )
                i++
            }
            data.add( strArray )
        }

        writer.writeAll( data )
        writer.close()
    }
}
