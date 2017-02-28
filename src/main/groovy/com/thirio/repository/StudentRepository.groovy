package com.thirio.repository

import com.thirio.model.Student
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository

/**
 * Created by lars.norlander on 2/27/17.
 */
@Repository
interface StudentRepository extends CrudRepository<Student, String>, QueryByExampleExecutor<Student> {

}