/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pad.basicmq.client.Client;
import com.isa.pas.basicmq.utils.Command;
import com.isa.pas.basicmq.utils.Message;
import com.isa.pas.basicmq.utils.Response;
import com.isa.pas.basicmq.utils.XMLSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Faust
 */
public class ClientHandler implements Runnable {

    private final Server server;
    private final Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;

    ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
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
                    String readCommand = readCommand();
                    Persister p = new Persister();
                    Command cmd = p.read(Command.class, new StringReader(readCommand));
                    if (cmd.getType().equals(Client.TYPE_SEND)) {
                        System.out.println("Sending message " + cmd.getBody());
                        server.getMessageBroker().addMessage(new Message(cmd.getBody()));
                    } else if (cmd.getType().equals(Client.TYPE_REQUEST)) {
                        Message message = server.getMessageBroker().getMessage();
                        StringWriter sw = XMLSerializer.serialize(new Response(message, "OK"));
                        output.println(sw.toString());
                        output.println();
                        output.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println(command);
        return command;
    }



}
