/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.demos;

import com.isa.pad.basicmq.client.Client;
import com.isa.pad.basicmq.client.MessageObserver;
import com.isa.pad.basicmq.server.ClientHandler;
import com.isa.pad.basicmq.utils.Command;
import com.isa.pad.basicmq.utils.Message;
import com.isa.pad.basicmq.utils.Response;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Faust
 */
public class DemoReceiver {

    public static void main(String[] args) {
        Client client = new Client("localhost", 9000);
        client.openConnection();
        client.subscribe(System.out::println, "GOOGLE");
        //Message msg = client.receiveMessage();
        //msg = client.receiveMessage("GOOGLE");
    }
}
