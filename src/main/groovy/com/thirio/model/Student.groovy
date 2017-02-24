package com.thirio.model

import groovy.transform.ToString

import javax.xml.bind.annotation.XmlElement
/**
 * @author patrick.pingol
 */
@ToString( includeNames = true )
class Student {
    String id

    @XmlElement( name = 'lastname' )
    String lastName

    @XmlElement( name = 'firstname' )
    String firstName

    String college

    String course

    @XmlElement( nillable = true )
    String status
}
