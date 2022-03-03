package com.itao.vertx.cli;

import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CLITest {

    public static void main(String[] args) {
        CLI cli = CLI.create("test")
                .addOption(
                        new Option().setLongName("help").setShortName("h").setFlag(true))
                .addOption(
                        new Option().setLongName("mandatory").setShortName("m").setDefaultValue("123"))
                .addArgument(
                        new Argument().setArgName("itao").setIndex(0).setRequired(true))
                .addArgument(
                        new Argument().setArgName("exit").setIndex(1));

        List<String> arguments = new ArrayList<>();
        arguments.add("test");
        arguments.add("itao");
        arguments.add("--help");
        arguments.add("-m");
        arguments.add("123456");
        CommandLine line = cli.parse(arguments);

        // The parsing does not fail and let you do:
        /*if (!line.isValid() && line.isAskingForHelp()) {
            StringBuilder builder = new StringBuilder();
            cli.usage(builder);
            System.out.print(builder);
        }*/

       /* while (true) {*/
            String mandatory = line.getOptionValue("mandatory");
            boolean flag = line.isFlagEnabled("help");
            String arg1 = line.getArgumentValue("exit");
            String arg0 = line.getArgumentValue("itao");
            System.out.println(mandatory);
            System.out.println(flag);
            System.out.println(arg0);
            System.out.println(arg1);
            /*if ("exit".equals(arg0)) {
                break;
            }
        }*/
    }
}
