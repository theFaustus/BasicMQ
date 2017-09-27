/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.isa.pad.basicmq.utils;

import java.io.StringWriter;
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
}
