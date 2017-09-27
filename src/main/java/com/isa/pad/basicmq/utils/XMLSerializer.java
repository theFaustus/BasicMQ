/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Faust
 */
public class XMLSerializer {

    public static <T> StringWriter serialize(T object) throws Exception {
        Persister p = new Persister();
        StringWriter sw = new StringWriter();
        p.write(object, sw);
        return sw;
    }

    public static <T> T deserialize(String text, Class<T> clazz) throws Exception {
        Persister p = new Persister();
        T rs = p.read(clazz, new StringReader(text));
        return rs;
    }
}
