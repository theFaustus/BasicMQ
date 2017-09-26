/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.demos;

import com.isa.pad.basicmq.client.Client;
import com.isa.pas.basicmq.utils.Message;

/**
 *
 * @author Faust
 */
public class DemoSender {
    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.openConnection();
        client.sendMessage(new Message("Hello"));
    }
}
