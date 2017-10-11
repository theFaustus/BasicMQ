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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static final String STATUS_MSG_PBL = "MSG_PBL";
    public static final String STATUS_MSG_OK = "MSG_OK";
    public static final String STATUS_MSG_ERROR = "MSG_ERROR";
    
    private static Map<String, Set<PrintWriter>> queueSubscribers = new ConcurrentHashMap<>();

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
                            Message message = new Message(cmd.getBody());
                            messageBroker.addMessage(message, cmd.getQueueName());
                            publishMessage(message, cmd.getQueueName());
                        } else if (cmd.getType().equals(Client.CMD_TYPE_RECEIVE)) {
                            Message message = messageBroker.getMessage(cmd.getQueueName());
                            StringWriter sw = XMLSerializer.serialize(new Response(message, STATUS_MSG_OK));
                            output.println(sw.toString());
                            output.println();
                            output.flush();
                        } else if (cmd.getType().equals(Client.CMD_TYPE_ACKNOWLEDGE)) {
                            messageBroker.purgeMessage(new Message(Long.valueOf(cmd.getBody())));
                        } else if (cmd.getType().equals(Client.CMD_TYPE_CREATE_QUEUE)) {
                            messageBroker.createQueueIfNotExists(cmd.getQueueName());
                        } else if (cmd.getType().equals(Client.CMD_TYPE_DELETE_QUEUE)) {
                            messageBroker.deleteQueueIfExists(cmd.getQueueName());
                        } else if (cmd.getType().equals(Client.CMD_TYPE_SUBSCRIBE)) {
                            String queueName = cmd.getQueueName();
                            subscribeTo(queueName);
                            while (messageBroker.hasMessages(queueName)) {
                                Message message = messageBroker.getMessage(queueName);
                                StringWriter sw = XMLSerializer.serialize(new Response(message, STATUS_MSG_PBL));
                                output.println(sw.toString());
                                output.println();
                                output.flush();
                            }

                        } else if (cmd.getType().equals(Client.CMD_TYPE_SEND_REGEX)) {
                            System.out.println("Sending message " + cmd.getBody());
                            Message message = new Message(cmd.getBody());
                            List<String> allQueuesNames = messageBroker.getAllQueues();
                            Pattern p = Pattern.compile(cmd.getQueueName());
                            for (String queuesName : allQueuesNames) {
                                Matcher m = p.matcher(queuesName);
                                if (m.find()) {
                                    messageBroker.addMessage(message, queuesName);
                                    publishMessage(message, queuesName);
                                }
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    StringWriter sw = XMLSerializer.serialize(new Response(null, STATUS_MSG_ERROR, e.getMessage()));
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

    private void subscribeTo(String queueName) {
        queueSubscribers.putIfAbsent(queueName, new CopyOnWriteArraySet<>());
        Set<PrintWriter> outputOfSubscriber = queueSubscribers.get(queueName);
        outputOfSubscriber.add(output);
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

    private void publishMessage(Message message, String queueName) throws Exception {
        for (Map.Entry<String, Set<PrintWriter>> entry : queueSubscribers.entrySet()) {
            String queue = entry.getKey();
            Set<PrintWriter> subs = entry.getValue();
            if (queue.equals(queueName)) {
                for (PrintWriter sub : subs) {
                    StringWriter sw = XMLSerializer.serialize(new Response(message, STATUS_MSG_PBL));
                    sub.println(sw.toString());
                    sub.println();
                    sub.flush();
                }
            }
        }
    }

}
