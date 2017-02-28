package com.thirio.repository

import com.thirio.model.Event
import com.thirio.model.Register
import com.thirio.model.Student
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by lars.norlander on 2/27/17.
 */
@Repository
interface RegisterRepository extends CrudRepository<Register, Integer> {

    Register findByStudentAndEvent(Student student, Event event)

}
