/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pas.basicmq.utils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author Faust
 */
@Root
public class Response {
    @Element
    private Message optionalMessage;
    @Element
    private String status;

    public Response() {
    }

    public Response(Message optionalMessage, String status) {
        this.optionalMessage = optionalMessage;
        this.status = status;
    }
    
    public Message getOptionalMessage() {
        return optionalMessage;
    }

    public void setOptionalMessage(Message optionalMessage) {
        this.optionalMessage = optionalMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
