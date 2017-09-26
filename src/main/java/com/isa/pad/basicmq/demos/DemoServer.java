/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.demos;

import com.isa.pad.basicmq.server.Server;

/**
 *
 * @author Faust
 */
public class DemoServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        Server basicMQServer = new Server(PORT);
        basicMQServer.startListening();
    }
}
