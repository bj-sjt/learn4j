package com.itao.jline;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;

import java.io.IOException;

public class AutoPairTest {
    public static void main(String[] args) throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
        DefaultParser parser = new DefaultParser();
        parser.setEofOnUnclosedBracket(
                DefaultParser.Bracket.CURLY,
                DefaultParser.Bracket.ROUND,
                DefaultParser.Bracket.SQUARE,
                DefaultParser.Bracket.ANGLE
        );
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                //.completer(completer)
                .parser(parser)
//                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
//                .variable(LineReader.INDENTATION, 2)   // indentation size
//                .option(LineReader.Option.INSERT_BRACKET, true)   // insert closing bracket automatically
                .build();
        // Create autopair widgets
        AutopairWidgets autopairWidgets = new AutopairWidgets(reader);
        // Enable autopair
        autopairWidgets.enable();

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
