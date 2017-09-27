/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pas.basicmq.utils;

/**
 *
 * @author Faust
 */
public class Message {
    private String body;

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
        return String.format("%s", body);
    }
    
    
    
}
