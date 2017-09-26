/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pas.basicmq.utils.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Faust
 */
public class MessageBroker {
    private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    
    public void addMessage(Message msg) throws InterruptedException{
        queue.put(msg);
    }
    
    public Message getMessage() throws InterruptedException{
        return queue.take();
    } 
}
