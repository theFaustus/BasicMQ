/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.client;

import com.isa.pas.basicmq.utils.Command;
import com.isa.pas.basicmq.utils.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Faust
 */
public class Client implements Runnable {

    private static final String TYPE_SEND = "send";

    private Socket socket;
    private String server;
    private int port;
    private InetAddress ip;
    private BufferedReader input;
    private PrintWriter output;
    private String response = "";
    private String command = "";
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void startCommunication() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            executorService.submit(this);

        } catch (IOException e) {
            System.out.println("Lost connection with server.");
        }
    }

    @Override
    public void run() {

    }

    public void sendMessage(Message msg) {
        try {
            Command cmd = new Command(TYPE_SEND, "default", msg.getBody());
            Persister p = new Persister();
            StringWriter sw = new StringWriter();
            p.write(cmd, sw);
            output.println(sw.toString());
            output.flush();
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openConnection() {
        System.out.println("Establishing connection... please wait.");
        while (socket == null) {
            try {
                ip = InetAddress.getByName(server);
                Socket socket = new Socket(ip, port);
                System.out.println("Server is online.");
                System.out.println("Connection established on " + server + ":" + port);
                this.socket = socket;
            } catch (UnknownHostException e) {
                System.out.println("The server is unknown.");
            } catch (IOException e) {
                System.out.println("Server is offline.");
            }
        }
    }

    private String validateOneLine() throws IOException {
        String response = input.readLine();
        if (response == null) {
            return null;
        }
        return response;
    }

}
