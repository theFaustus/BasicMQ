/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pad.basicmq.utils.DBConnector;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.MessageBrokerDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Faust
 */
public class MessageBroker {

    public static final String DEFAULT_QUEUE_NAME = "default";

    private Map<String, BlockingQueue> queues = new ConcurrentHashMap<>();
    private MessageBrokerDAO brokerDAO = new MessageBrokerDAO(new DBConnector());

    public MessageBroker() {
        queues = brokerDAO.loadQueues();
        createQueueIfNotExists(DEFAULT_QUEUE_NAME);
    }

    public List<String> getAllQueues() {
        return new ArrayList<>(queues.keySet());
    }

    public void createQueueIfNotExists(String queueName) {
        if (!brokerDAO.isQueuePresent(queueName)) {
            brokerDAO.saveQueue(queueName);
            queues.put(queueName, new LinkedBlockingQueue());
        }

    }

    public void deleteQueueIfExists(String queueName) {
        if (brokerDAO.isQueuePresent(queueName)) {
            brokerDAO.deleteMessagesByQueueId(queueName);
            brokerDAO.deleteQueue(queueName);
        }
        queues.remove(queueName);
    }

    public void addMessage(Message msg, String queueName) throws InterruptedException {
        msg.setQueueName(queueName);
        brokerDAO.saveMessage(msg);
        BlockingQueue<Message> queue = queues.get(queueName);
        if (queue != null) {
            queue.put(msg);
        } else {
            throw new RuntimeException("Queue [" + queueName + "] doesn`t exist.");
        }
    }

    public void addMessage(Message msg) throws InterruptedException {
        msg.setQueueName(DEFAULT_QUEUE_NAME);
        brokerDAO.saveMessage(msg);
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        queue.put(msg);
    }

    public void purgeMessage(Message msg) throws InterruptedException {
        brokerDAO.deleteMessage(msg);
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        queue.remove(msg);
    }

    public void deleteMessage(Message msg) throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        queue.remove(msg);
    }

    public void purgeMessage(Message msg, String queueName) throws InterruptedException {
        brokerDAO.deleteMessage(msg);
        BlockingQueue<Message> queue = queues.get(queueName);
        queue.remove(msg);
    }

    public void deleteMessage(Message msg, String queueName) throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(queueName);
        queue.remove(msg);
    }

    public boolean hasMessages(String queueName) throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(queueName);
        return !queue.isEmpty();
    }

    public Message getMessage(String queueName) throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(queueName);
        if (queue != null) {
            return queue.take();
        } else {
            throw new RuntimeException("Queue [" + queueName + "] doesn`t exist.");
        }
    }

    public Message getMessage() throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        return queue.take();
    }
}
