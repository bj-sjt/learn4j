package com.itao.springshell.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;

@ShellComponent
public class MyCommand {

    /**
     * add 10 20              // 30
     * add --a 10 --b 20      // 30
     */
    @ShellMethod(value = "Add two integers together.")
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * sum 10 20               // 30
     * sum -a 10 -b 20         // 30
     */
    @ShellMethod(value = "Sum two integers together.", prefix = "-")
    public int sum(int a, int b) {
        return a + b;
    }

    /**
     * echo 10 20 30                       // 60
     * echo 10 20 --third 30               // 60
     * echo -a 10 -b 20 --third 30         // 60
     */
    @ShellMethod(value = "Display stuff.", prefix = "-")
    public int echo(int a, int b, @ShellOption("--third") int c) {
        return a + b + c;
    }

    /**
     * print 10 20 30                       // 60
     * print -a 10 -b 20 -c 30              // 60
     * print 10 20 --third 30               // 60
     * print -a 10 -b 20 --third 30         // 60
     */
    @ShellMethod(value = "Display stuff.", prefix = "-")
    public int print(int a, int b, @ShellOption({"--third", "-c"}) int c) {
        return a + b + c;
    }

    /**
     * greet                          // hello world
     * greet --who world              // hello world
     */
    @ShellMethod("Say hello")
    public String greet(@ShellOption(defaultValue = "world") String who) {
        return "hello " + who;
    }

    /**
     * agg 10 20 30                  // 60.0
     * agg --numbers 10 20 30        // 60.0
     */
    @ShellMethod("agg Numbers.")
    public float agg(@ShellOption(arity = 3, help = "只能三个参数") float[] numbers) {
        float sum = 0;
        for (float f : numbers) {
            sum += f;
        }
        return sum;
    }

    /**
     * change-password --password 'hello world'      // Password successfully set to hello world
     */
    @ShellMethod("Change password.")
    public String changePassword(@Size(min = 8, max = 40) String password) {
        return "Password successfully set to " + password;
    }
}
