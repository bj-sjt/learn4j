package com.itao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
    public static void main(String[] args) throws IOException {
        InputStream is = Version.class.getClassLoader().getResourceAsStream("learn4j-version.txt");
        Properties prop = new Properties();
        prop.load(is);
        String version = prop.getProperty("version");
        String var = prop.getProperty("var");
        System.out.println(version);
        System.out.println(var);
    }
}
