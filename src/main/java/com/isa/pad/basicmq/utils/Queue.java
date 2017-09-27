/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

/**
 *
 * @author Faust
 */
public class Queue {

    private Long id;
    private String queueName;

    public Queue() {
    }

    public Queue(Long id, String queueName) {
        this.id = id;
        this.queueName = queueName;
    }
    
    

    public Queue(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

}
