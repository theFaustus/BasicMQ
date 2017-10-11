/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.client;

import com.isa.pad.basicmq.server.ClientHandler;
import com.isa.pad.basicmq.server.MessageBroker;
import com.isa.pad.basicmq.utils.Command;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.Response;
import com.isa.pad.basicmq.utils.XMLSerializer;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Faust
 */
public class Client implements Runnable, Closeable {

    public static final String CMD_TYPE_SEND = "send";
    public static final String CMD_TYPE_SEND_REGEX = "send_regex";
    public static final String CMD_TYPE_RECEIVE = "receive";
    public static final String CMD_TYPE_ACKNOWLEDGE = "acknowledge";
    public static final String CMD_TYPE_CREATE_QUEUE = "create_queue";
    public static final String CMD_TYPE_DELETE_QUEUE = "delete_queue";
    public static final String CMD_TYPE_SUBSCRIBE = "subscribe";

    private Socket socket;
    private String server;
    private int port;
    private InetAddress ip;
    private BufferedReader input;
    private PrintWriter output;
    private String response = "";
    private String command = "";
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private BlockingQueue<Response> responsesToConsume = new ArrayBlockingQueue<>(10);
    private volatile boolean closeRequested = false;
    private Map<String, Set<MessageObserver>> messageObservers = new HashMap<>();

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void close() throws IOException {
        closeRequested = true;
        socket.close();
        output.close();
        executorService.shutdown();
    }

    @Override
    public void run() {
        while (!closeRequested) {
            try {
                String readCommand = readCommand();
                Response rs = XMLSerializer.deserialize(readCommand, Response.class);
                if (rs.getStatus().equals(ClientHandler.STATUS_MSG_PBL)) {
                    Message msg = rs.getOptionalMessage();
                    messageObservers.get(msg.getQueueName()).forEach(a -> a.consumeMessage(msg));
                    acknowledgeMessage(msg);
                } else {
                    responsesToConsume.put(rs);
                }
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public void subscribe(MessageObserver o, String queueName) {
        messageObservers.putIfAbsent(queueName, new HashSet<>());
        messageObservers.get(queueName).add(o);
        sendCommand(new Command(CMD_TYPE_SUBSCRIBE, queueName, ""));

    }

    public void sendMessage(Message msg) {
        try {
            msg.setQueueName(MessageBroker.DEFAULT_QUEUE_NAME);
            Command cmd = new Command(CMD_TYPE_SEND, MessageBroker.DEFAULT_QUEUE_NAME, msg.getBody());
            sendCommand(cmd);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(Message msg, String queueName) {
        try {
            msg.setQueueName(queueName);
            Command cmd = new Command(CMD_TYPE_SEND, queueName, msg.getBody());
            sendCommand(cmd);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendRegexMessage(Message msg, String regex) {
        try {
            msg.setQueueName(regex);
            Command cmd = new Command(CMD_TYPE_SEND_REGEX, regex, msg.getBody());
            sendCommand(cmd);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createQueue(String queueName) {
        try {
            Command cmd = new Command(CMD_TYPE_CREATE_QUEUE, queueName, null);
            sendCommand(cmd);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteQueue(String queueName) {
        try {
            Command cmd = new Command(CMD_TYPE_DELETE_QUEUE, queueName, null);
            sendCommand(cmd);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendCommand(Command cmd) {
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
            sendCommand(new Command(CMD_TYPE_RECEIVE, MessageBroker.DEFAULT_QUEUE_NAME, ""));
            Response rs = responsesToConsume.take();
            Message message = rs.getOptionalMessage();
            System.out.println("Message received " + message);
            acknowledgeMessage(message);
            return message;

        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Message receiveMessage(String queueName) {
        try {
            sendCommand(new Command(CMD_TYPE_RECEIVE, queueName, ""));
            Response rs = responsesToConsume.take();
            Message message = rs.getOptionalMessage();
            message.setQueueName(queueName);
            System.out.println("Message received " + message);
            acknowledgeMessage(message);
            return message;

        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void acknowledgeMessage(Message msg) {
        sendCommand(new Command(CMD_TYPE_ACKNOWLEDGE, msg.getQueueName(), String.valueOf(msg.getId())));
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
