/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 *
 * @author Faust
 */
public class Server {

    private int port;
    private ServerSocket serverSocket;
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private Map<String, Socket> connectedUsers = new HashMap<>();

    public Server(int port) {
        System.out.println("BasicMQ Server 1.0");
        System.out.println("Listening on port " + port);
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.out.println("System exception!");
        }
    }

    public void startListening() {
        try {
            while (true) {
                Socket s = serverSocket.accept();
                System.out.println("Connection established!");
                this.executor.execute(new ClientHandler(this, s));
            }
        } catch (Exception e) {
            System.out.println("System exception!");
        }
    }

    public Map<String, Socket> getConnectedUsers() {
        return connectedUsers;
    }

}
