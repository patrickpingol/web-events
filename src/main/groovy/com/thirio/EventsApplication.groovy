package com.thirio

import com.thirio.model.Event
import com.thirio.model.Register
import com.thirio.model.Student
import com.thirio.repository.EventRepository
import com.thirio.repository.RegisterRepository
import com.thirio.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class EventsApplication {

	@Autowired
	private static RegisterRepository registerRepository

	@Autowired
	private static StudentRepository studentRepository

	@Autowired
	private static EventRepository eventRepository

	static void main(String[] args) {
		SpringApplication.run EventsApplication, args

		Event event = new Event()
		event.name = 'fuckshit'
		event.date = new Date()
		event = eventRepository.save(event)

		Student student = new Student()
		student.id = "fuck"
		student.college = "ccs"
		student.course = "bscs"
		student.firstName = "lars"
		student.lastName = "norlander"
		student.status = "wat"
		student = studentRepository.save(student)

		Register register = new Register()
		register.student = student
		register.event = event
		register.isPresent = true
		registerRepository.save(register)
	}
}
