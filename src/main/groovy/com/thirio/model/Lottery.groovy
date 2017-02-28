package com.thirio.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Created by lars.norlander on 2/28/17.
 */
@Entity
class Lottery {

    @Id
    @GeneratedValue
    Integer id

    @ManyToOne
    Event event

    @ManyToOne
    Student student

}
