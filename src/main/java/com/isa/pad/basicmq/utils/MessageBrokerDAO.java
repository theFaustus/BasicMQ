/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Faust
 */
public class MessageBrokerDAO {

    private static final String INSERT_MESSAGE_SQL = "INSERT INTO message (body, queue_id) VALUES (?, ?)";
    private static final String DELETE_MESSAGE_SQL = "DELETE FROM message WHERE id = ?";
    private static final String INSERT_QUEUE_SQL = "INSERT INTO queue (name) VALUES (?)";
    private static final String DELETE_QUEUE_SQL = "DELETE FROM queue WHERE id = ?";
    private static final String SELECT_ALL_MESSAGES_SQL = "SELECT id, body FROM message";
    private static final String SELECT_MESSAGES_BY_QUEUE_ID_SQL = "SELECT id, body FROM message WHERE queue_id = ?";
    private static final String SELECT_QUEUE_BY_NAME_SQL = "SELECT id, name FROM queue WHERE name = ?";
    private static final String SELECT_ALL_QUEUES_SQL = "SELECT id, name FROM queue";
    private static final String SELECT_QUEUE_ID_BY_NAME_SQL = "SELECT id FROM queue WHERE name = ?";

    private DBConnector dBConnector;

    public MessageBrokerDAO(DBConnector dBConnector) {
        this.dBConnector = dBConnector;
    }

    public Message saveMessage(Message msg) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(INSERT_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, msg.getBody());
                statement.setLong(2, findQueueIdByName(new Queue(msg.getQueueName())));
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                msg.setId(rs.getLong(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }

    public void deleteMessage(Message msg) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(DELETE_MESSAGE_SQL)) {
                statement.setLong(1, msg.getId());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Queue saveQueue(Queue queue) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(INSERT_QUEUE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, queue.getQueueName());
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                queue.setId(rs.getLong(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queue;
    }

    public Queue deleteQueue(Queue queue) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(DELETE_QUEUE_SQL)) {
                statement.setLong(1, queue.getId());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queue;
    }

    public Map<String, BlockingQueue> loadQueues() {
        Map<String, BlockingQueue> queues = new ConcurrentHashMap<>();
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(SELECT_ALL_QUEUES_SQL)) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    queues.put(rs.getString(2), loadMessagesByQueueId(rs.getLong(1)));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return queues;
    }

    public BlockingQueue<Message> loadMessagesByQueueId(Long id) {
        BlockingQueue messages = new LinkedBlockingDeque();
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(SELECT_MESSAGES_BY_QUEUE_ID_SQL)) {
                statement.setLong(1, id);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    messages.add(new Message(rs.getLong(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return messages;
    }

    public boolean isQueuePresent(Queue q) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(SELECT_QUEUE_BY_NAME_SQL)) {
                statement.setString(1, q.getQueueName());
                ResultSet rs = statement.executeQuery();
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Long findQueueIdByName(Queue q) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(SELECT_QUEUE_ID_BY_NAME_SQL)) {
                statement.setString(1, q.getQueueName());
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new RuntimeException("Queue " + q.getQueueName() + " doesn`t exist.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
