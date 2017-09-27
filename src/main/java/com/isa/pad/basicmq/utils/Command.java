/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author Faust
 */
@Root
public class Command {

    @Element
    private String type;
    @Element
    private String queueName;
    @Element(required = false)
    private String body;

    public Command(String type, String queueName, String body) {
        this.type = type;
        this.queueName = queueName;
        this.body = body;
    }

    public Command() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Command [type=" + type + ", queueName=" + queueName + ", body=" + body + "]";
    }

}
