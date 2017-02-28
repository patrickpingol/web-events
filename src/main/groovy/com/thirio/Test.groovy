package com.thirio

import com.thirio.model.Event
import com.thirio.model.Lottery
import com.thirio.model.Register
import com.thirio.model.Student
import com.thirio.repository.EventRepository
import com.thirio.repository.LotteryRepository
import com.thirio.repository.RegisterRepository
import com.thirio.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
/**
 * Created by lars.norlander on 2/28/17.
 */
@Component
class Test {

    @Autowired
    private RegisterRepository registerRepository

    @Autowired
    private StudentRepository studentRepository

    @Autowired
    private EventRepository eventRepository

    @Autowired
    private LotteryRepository lotteryRepository

    @PostConstruct
    void testData() {
        Event event = new Event(
                name: 'CAMP Night',
                date: new Date())
        event = eventRepository.save(event)

        Student student = new Student(
                id: '13-0833-205',
                college: 'CCS',
                course: 'BSCS',
                firstName: 'Lars Joseph',
                lastName: 'Norlander')
        student = studentRepository.save(student)

        Register register = new Register(
                student: student,
                event: event,
                isPresent: true,
                time: new Date())
        registerRepository.save(register)

        Lottery lottery = new Lottery(
                student: student,
                event: event)
        lotteryRepository.save(lottery)
    }

}
