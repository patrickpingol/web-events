package com.thirio.service

import com.thirio.model.Event
import com.thirio.repository.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by lars.norlander on 2/28/17.
 */
@Service
class EventService {

    @Autowired
    private EventRepository eventRepository

    Event createEvent(Event event){
        eventRepository.save(event)
    }

    List<Event> getEventList(String name, Date date){
        if(name && date) return eventRepository.findByNameLikeAndDate(name, date)
        if(!name && !date) return (List<Event>) eventRepository.findAll()
        if (name) return eventRepository.findByNameLike(name)
        return eventRepository.findByDate(date)
    }

    Event getEvent(Integer id){
        eventRepository.findOne(id)
    }

    void deleteEvent(Integer id){
        eventRepository.delete(id)
    }

    void deleteEvent(Event event){
        eventRepository.delete(event)
    }
}
