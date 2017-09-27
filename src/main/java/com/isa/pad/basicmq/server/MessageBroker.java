/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pad.basicmq.utils.DBConnector;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.MessageBrokerDAO;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Faust
 */
public class MessageBroker {
    private BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private MessageBrokerDAO brokerDAO = new MessageBrokerDAO(new DBConnector());
    
    public void addMessage(Message msg) throws InterruptedException{
        brokerDAO.saveMessage(msg);
        queue.put(msg);
    }
    
    public Message getMessage() throws InterruptedException{
       
        return queue.take();
    } 
}
