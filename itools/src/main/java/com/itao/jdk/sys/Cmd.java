package com.itao.jdk.sys;

import java.io.IOException;

public class Cmd {
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("C:/Windows/System32/cmd.exe /k jconsole");
    }
}
