/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

/**
 *
 * @author Faust
 */
public class ConnectionHandler implements Runnable {

    private final BasicMQServer bServer;
    private final Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;

    ConnectionHandler(BasicMQServer bMQServer, Socket clientSocket) {
        this.bServer = bMQServer;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            String client = clientSocket.getInetAddress().toString();
            System.out.println("Connected to " + client + " " + Thread.currentThread().getName());
              
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            output.println("Welcome to BasicMQ Server");
            
            while (true) {
                if (input.ready()) {
                    String command = input.readLine();
                    System.out.println(command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
