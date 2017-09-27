/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.client;

import com.isa.pad.basicmq.utils.Message;

/**
 *
 * @author Faust
 */
public interface MessageObserver {
    void consumeMessage(Message msg);
}
