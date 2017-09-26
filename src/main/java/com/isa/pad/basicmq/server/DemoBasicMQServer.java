/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

/**
 *
 * @author Faust
 */
public class DemoBasicMQServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        BasicMQServer basicMQServer = new BasicMQServer(PORT);
        basicMQServer.startListening();
    }
}
