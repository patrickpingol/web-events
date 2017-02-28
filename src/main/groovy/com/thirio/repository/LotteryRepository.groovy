package com.thirio.repository

import com.thirio.model.Event
import com.thirio.model.Lottery
import com.thirio.model.Student
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by lars.norlander on 2/28/17.
 */
@Repository
interface LotteryRepository extends CrudRepository<Lottery, Integer> {

    Lottery findByEventAndStudent(Event event, Student student)

}