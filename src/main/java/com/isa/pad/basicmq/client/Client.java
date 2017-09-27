/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.client;

import com.isa.pad.basicmq.utils.Command;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.Response;
import com.isa.pad.basicmq.utils.XMLSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
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
public class Client implements Runnable, AutoCloseable {

    public static final String CMD_TYPE_SEND = "send";
    public static final String CMD_TYPE_RECEIVE = "receive";

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

    public void close() throws IOException {
        socket.close();
        output.close();
        executorService.shutdown();
    }

    @Override
    public void run() {

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

                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                executorService.submit(this);

            } catch (UnknownHostException e) {
                System.out.println("The server is unknown.");
            } catch (IOException e) {
                System.out.println("Server is offline.");
            }
        }
    }

    public void sendMessage(Message msg) {
        try {
            Command cmd = new Command(CMD_TYPE_SEND, "default", msg.getBody());
            StringWriter sw = XMLSerializer.serialize(cmd);
            output.println(sw.toString());
            output.println();
            output.flush();
            System.out.println("Message sent.");
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendRequest(Command cmd) {
        StringWriter sw;
        try {
            sw = XMLSerializer.serialize(cmd);
            output.println(sw.toString());
            output.println();
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage() {
        try {
            while (true) {
                sendRequest(new Command(CMD_TYPE_RECEIVE, "default", ""));
                String readCommand = readCommand();
                Persister p = new Persister();
                Response rs = p.read(Response.class, new StringReader(readCommand));
                Message message = new Message(rs.getOptionalMessage().getBody());
                System.out.println("Message received " + message.getBody());
                return message;
            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private String readCommand() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = input.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sb.append(line);
        }
        String command = sb.toString();
        return command;
    }

}
