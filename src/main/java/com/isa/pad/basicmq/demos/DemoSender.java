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
        client.sendMessage(new Message("Hello"));
        client.createQueue("GOOGLE");
        client.sendMessage(new Message("Hello World!"), "GOOGLE");
    }
}
