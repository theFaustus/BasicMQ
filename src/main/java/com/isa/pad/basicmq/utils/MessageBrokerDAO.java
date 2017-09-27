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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Faust
 */
public class MessageBrokerDAO {

    private static final String INSERT_MESSAGE_SQL = "INSERT INTO message (body) VALUES (?)";
    private static final String DELETE_MESSAGE_SQL = "DELETE FROM message WHERE id = ?";
    private static final String INSERT_QUEUE_SQL = "INSERT INTO queue (name) VALUES (?)";
    private static final String DELETE_QUEUE_SQL = "DELETE FROM queue WHERE id = ?";
    private static final String SELECT_ALL_MESSAGES_SQL = "SELECT id, body FROM message";

    private DBConnector dBConnector;

    public MessageBrokerDAO(DBConnector dBConnector) {
        this.dBConnector = dBConnector;
    }

    public Message saveMessage(Message msg) {
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(INSERT_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, msg.getBody());
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

    public List<Message> loadMessages() {
        List<Message> messages = new ArrayList<>();
        try (Connection c = dBConnector.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(SELECT_ALL_MESSAGES_SQL)) {
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    messages.add(new Message(rs.getLong(1), rs.getString(2)));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageBrokerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return messages;
    }

}
