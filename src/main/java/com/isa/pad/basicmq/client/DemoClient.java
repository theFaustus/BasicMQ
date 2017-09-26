/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.client;

import com.isa.pas.basicmq.utils.Message;

/**
 *
 * @author Faust
 */
public class DemoClient {
    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.openConnection();
        client.startCommunication();
        client.sendMessage(new Message("Hello"));
    }
}
