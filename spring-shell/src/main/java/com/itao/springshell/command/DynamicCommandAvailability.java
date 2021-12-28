package com.itao.springshell.command;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class DynamicCommandAvailability {

    private boolean connected;

    @ShellMethod("Connect to the server.")
    public void connect1(String user, String password) {
        connected = true;
    }

    @ShellMethod("Download the nuclear codes.")
    public void download1() {
    }
    @ShellMethod("Check the nuclear codes.")
    public void check1() {
    }

    public Availability downloadAvailability() {
        return connected
            ? Availability.available()
            : Availability.unavailable("you are not connected");
    }

    public Availability checkAvailability() {
        return connected
                ? Availability.available()
                : Availability.unavailable("you are not connected");
    }


    @ShellMethod("Connect to the server.")
    public void connect2(String user, String password) {
        connected = true;
    }

    @ShellMethod("Download the nuclear codes.")
    @ShellMethodAvailability("availability")
    public void download2() {
    }
    @ShellMethod("Check the nuclear codes.")
    @ShellMethodAvailability("availability")
    public void check2() {
    }

    public Availability availability() {
        return connected
                ? Availability.available()
                : Availability.unavailable("you are not connected");
    }


    @ShellMethod("Connect to the server.")
    public void connect3(String user, String password) {
        connected = true;
    }

    @ShellMethod("Download the nuclear codes.")
    public void download3() {
    }
    @ShellMethod("Check the nuclear codes.")
    public void check3() {
    }

    @ShellMethodAvailability({"download3", "check3"})
    public Availability availability3() {
        return connected
                ? Availability.available()
                : Availability.unavailable("you are not connected");
    }
}