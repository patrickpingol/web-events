package com.thirio.repository

import com.thirio.model.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by lars.norlander on 2/27/17.
 */
@Repository
interface EventRepository extends CrudRepository<Event, Integer>{


}
