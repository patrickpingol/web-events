package com.thirio.model

import groovy.transform.Canonical
import groovy.transform.ToString

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement

/**
 * @author patrick.pingol
 */
@XmlAccessorType( XmlAccessType.FIELD )
@Canonical
@ToString( includeNames = true )
class Event {

    @XmlElement( nillable = true )
    Integer id

    String name

    String date

    Event() {
    }

    Event( Integer id, String name, String date ) {
        this.id = id
        this.name = name
        this.date = date
    }

    Event( String name, String date ) {
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

    void setDate( String date ) {
        this.date = date
    }
}
