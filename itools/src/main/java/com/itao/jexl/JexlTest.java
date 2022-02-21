package com.itao.jexl;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;

import java.util.Collections;

public class JexlTest {

    public static void main(String[] args) {
        JexlEngine jexl = new JexlEngine();
        String jexlExp = "(1 + 2) * 3";
        //String jexlExp = "(1+2)*pi";
        Script e =  jexl.createScript(jexlExp);
        JexlContext jc = new MapContext();
        //JexlContext jc = new MapContext(Collections.singletonMap("pi", Math.PI));
        //jc.set("pi", Math.PI);
        Object execute = e.execute(jc);
        System.out.println(execute); // 9
    }
}
