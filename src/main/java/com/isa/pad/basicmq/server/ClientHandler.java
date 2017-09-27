/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pad.basicmq.client.Client;
import com.isa.pad.basicmq.utils.Command;
import com.isa.pad.basicmq.utils.DBConnector;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.MessageBrokerDAO;
import com.isa.pad.basicmq.utils.Queue;
import com.isa.pad.basicmq.utils.Response;
import com.isa.pad.basicmq.utils.XMLSerializer;
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
    private MessageBrokerDAO brokerDAO;
    private MessageBroker messageBroker;

    ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.messageBroker = server.getMessageBroker();
    }

    @Override
    public void run() {
        try {
            String client = clientSocket.getInetAddress().toString();
            System.out.println("Connected to " + client + " " + Thread.currentThread().getName());

            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            brokerDAO = new MessageBrokerDAO(new DBConnector());

            while (true) {
                try {
                    if (input.ready()) {
                        String readCommand = readCommand();
                        Command cmd = XMLSerializer.deserialize(readCommand, Command.class);
                        if (cmd.getType().equals(Client.CMD_TYPE_SEND)) {
                            System.out.println("Sending message " + cmd.getBody());
                            messageBroker.addMessage(new Message(cmd.getBody()), new Queue(cmd.getQueueName()));
                        } else if (cmd.getType().equals(Client.CMD_TYPE_RECEIVE)) {
                            Message message = messageBroker.getMessage(new Queue(cmd.getQueueName()));
                            StringWriter sw = XMLSerializer.serialize(new Response(message, "OK"));
                            output.println(sw.toString());
                            output.println();
                            output.flush();
                        } else if (cmd.getType().equals(Client.CMD_TYPE_ACKNOWLEDGE)) {
                            messageBroker.deleteMessage(new Message(Long.valueOf(cmd.getBody())));
                        } else if (cmd.getType().equals(Client.CMD_TYPE_CREATE_QUEUE)) {
                            messageBroker.createQueueIfNotExists(new Queue(cmd.getQueueName()));
                        } else if (cmd.getType().equals(Client.CMD_TYPE_DELETE_QUEUE)) {
                            messageBroker.deleteQueueIfExists(new Queue(cmd.getQueueName()));
                        }
                    }
                } catch (RuntimeException e) {
                    StringWriter sw = XMLSerializer.serialize(new Response(null, "ERROR", e.getMessage()));
                    output.println(sw.toString());
                    output.println();
                    output.flush();
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
