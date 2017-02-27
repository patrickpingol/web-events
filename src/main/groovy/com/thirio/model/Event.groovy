package com.thirio.model

import groovy.transform.Canonical
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

/**
 * @author patrick.pingol
 */
@Entity
@XmlAccessorType( XmlAccessType.FIELD )
@Canonical
@ToString( includeNames = true )
class Event {

    @Id
    @GeneratedValue
    @XmlElement( nillable = true )
    Integer id

    @Column
    String name

    @Column
    public Date date

    Event() {
    }

    Event( Integer id, String name, Date date ) {
        this.id = id
        this.name = name
        this.date = date
    }

    Event( String name, Date date ) {
        this.name = name
        this.date = date
    }

    Integer getId() {
        return id
    }

    void setId( Integer id ) {
        this.id = id
    }

    String getName() {
        return name
    }

    void setName( String name ) {
        this.name = name
    }

    String getDate() {
        return date
    }

    void setDate( Date date ) {
        this.date = date
    }
}
