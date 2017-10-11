/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.demos;

import com.isa.pad.basicmq.client.Client;
import com.isa.pad.basicmq.utils.Message;
import java.io.IOException;

/**
 *
 * @author Faust
 */
public class DemoSender {

    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.openConnection();
        Message message = new Message("Hello!");
        client.sendMessage(message);
        System.out.println("Sent " + message);
        client.createQueue("GOOGLE");
        message = new Message("Hello World!");
        client.sendMessage(message, "GOOGLE");
        System.out.println("Sent " + message);
        client.createQueue("GOOGLPLEX");
        message = new Message("Hello Galaxy!");
        client.sendRegexMessage(message, "G.+");
        System.out.println("Sent " + message);
    }
}
