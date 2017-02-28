package com.thirio.model

import javax.persistence.*

/**
 * Created by lars.norlander on 2/27/17.
 */
@Entity
class Register {

    @Id
    @GeneratedValue
    Integer id

    @ManyToOne
    Event event

    @ManyToOne
    Student student

    @Column
    boolean isPresent

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date inTimestamp

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date outTimestamp

}
