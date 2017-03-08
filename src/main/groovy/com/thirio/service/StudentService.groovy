package com.thirio.service

import au.com.bytecode.opencsv.CSVReader
import com.thirio.model.Student
import com.thirio.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * Created by lars.norlander on 2/28/17.
 */
@Service
class StudentService {

    @Autowired
    private StudentRepository studentRepository

    Student createStudent(Student student) {
        studentRepository.save(student)
    }

    Boolean createStudents(MultipartFile file) {
        File newFile = new File('/tmp/' + file.getOriginalFilename())
        file.transferTo(newFile)
        CSVReader reader = new CSVReader(new FileReader(newFile))
        List<Student> students = []
        reader.readAll().each {it ->
            students << new Student(
                    id: it[0], lastName: it[1], firstName: it[2],
                    college: it[3], course: it[4])
        }
        studentRepository.save(students)
    }

    List<Student> getStudentList(String id, String lastName, String firstName, String college, String course) {
        ExampleMatcher studentMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        (List<Student>) studentRepository.findAll(Example.of(new Student(
                id: id,
                lastName: lastName,
                firstName: firstName,
                college: college,
                course: course), studentMatcher))
    }

    Student getStudentById(String id){
        studentRepository.findOne(id)
    }

    void deleteStudent(String id){
        studentRepository.delete(id)
    }
}
