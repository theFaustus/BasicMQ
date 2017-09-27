/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.server;

import com.isa.pad.basicmq.utils.DBConnector;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.MessageBrokerDAO;
import com.isa.pad.basicmq.utils.Queue;
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

    private static final String DEFAULT_QUEUE_NAME = "default";

    private Map<String, BlockingQueue> queues = new ConcurrentHashMap<>();
    private MessageBrokerDAO brokerDAO = new MessageBrokerDAO(new DBConnector());

    public MessageBroker() {
        createQueueIfNotExists(new Queue(DEFAULT_QUEUE_NAME));
    }

    public void createQueueIfNotExists(Queue queue) {
        if (!brokerDAO.isQueuePresent(queue)) {
            queues.put(queue.getQueueName(), new LinkedBlockingQueue());
            brokerDAO.saveQueue(queue);
        }
    }

    public void addMessage(Message msg, Queue q) throws InterruptedException {
        brokerDAO.saveMessage(msg);
        BlockingQueue<Message> queue = queues.get(q.getQueueName());
        if (queue != null) {
            queue.put(msg);
        } else {
            throw new RuntimeException("Queue [" + q.getQueueName() + "] doesn`t exist.");
        }
    }

    public void addMessage(Message msg) throws InterruptedException {
        brokerDAO.saveMessage(msg);
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        queue.put(msg);
    }

    public Message getMessage(Queue q) throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(q.getQueueName());
        if (queue != null) {
            return queue.take();
        } else {
            throw new RuntimeException("Queue [" + q.getQueueName() + "] doesn`t exist.");
        }
    }

    public Message getMessage() throws InterruptedException {
        BlockingQueue<Message> queue = queues.get(DEFAULT_QUEUE_NAME);
        return queue.take();
    }
}
