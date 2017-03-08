package com.thirio.service

import com.sun.javaws.exceptions.InvalidArgumentException
import com.thirio.model.Event
import com.thirio.model.Register
import com.thirio.model.Student
import com.thirio.repository.EventRepository
import com.thirio.repository.RegisterRepository
import com.thirio.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by lars.norlander on 2/28/17.
 */

// TODO: Implement liveAttendeesCount
@Service
class RegisterService {

    @Autowired
    private RegisterRepository registerRepository

    @Autowired
    private StudentRepository studentRepository

    @Autowired
    private EventRepository eventRepository

    // TODO: Implement not Eligible logic
    Register registerStudent(String studentId, Integer eventId, Boolean notEligible) {
        Student student = studentRepository.findOne(studentId)
        if (!student) throw new InvalidArgumentException("No such student.")

        Event event = eventRepository.findOne(eventId)
        if (!event) throw new InvalidArgumentException("No such event.")

        Register register = registerRepository.findByStudentAndEvent(student, event)
        if (!register) return registerRepository.save(new Register(
                student: student,
                event: event,
                isPresent: true,
                inTimestamp: new Date()))

        register.isPresent = !register.isPresent
        if(!register.isPresent) register.outTimestamp = new Date()
        return registerRepository.save(register)
    }
}
