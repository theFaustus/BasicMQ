/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author Faust
 */
@Root
public class Message {

    @Attribute
    private Long id;
    @Element
    private String body;

    public Message() {
    }

    public Message(Long id) {
        this.id = id;
    }

    public Message(Long id, String body) {
        this.id = id;
        this.body = body;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", body=" + body + '}';
    }    

}
