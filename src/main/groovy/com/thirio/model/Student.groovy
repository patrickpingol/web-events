package com.thirio.model

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.xml.bind.annotation.XmlElement
/**
 * @author patrick.pingol
 */
@Entity
@ToString( includeNames = true )
class Student {

    @Id
    String id

    @Column
    @XmlElement( name = 'lastname' )
    String lastName

    @Column
    @XmlElement( name = 'firstname' )
    String firstName

    @Column
    String college

    @Column
    String course
}
