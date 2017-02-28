package com.thirio.service

import com.thirio.model.Event
import com.thirio.model.Lottery
import com.thirio.model.Register
import com.thirio.model.Student
import com.thirio.repository.EventRepository
import com.thirio.repository.LotteryRepository
import com.thirio.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by lars.norlander on 2/28/17.
 */
@Service
class LotteryService {
    @Autowired
    private LotteryRepository lotteryRepository

    @Autowired
    private StudentRepository studentRepository

    @Autowired
    private EventRepository eventRepository

    Student insertToLotteryTable(Integer eventId) {
        Event event = eventRepository.findOne(eventId)
        while (true) {
            Collections.shuffle(event.registeredStudents)
            Register register = event.registeredStudents.first()
            if (!register.isPresent) continue
            Student student = register.student
            if (lotteryRepository.findByEventAndStudent(event, student)) continue
            lotteryRepository.save(new Lottery(event: event, student: student))
            return student
        }
    }

}
