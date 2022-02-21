package com.itao.jline;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutosuggestionWidgets;

import java.io.IOException;

public class AutosuggestionTest {
    public static void main(String[] args) throws IOException {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(TerminalBuilder.terminal())
                //.completer(completer)
                //.parser(parser)
                .build();
        // Create autosuggestion widgets
        //AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(reader);
        // Enable autosuggestions
       // autosuggestionWidgets.enable();

        String prompt = "itao:>";
        while (true) {
            String line = null;
            try {
                line = reader.readLine(prompt);
                System.out.println(line);
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
        }
    }
}
