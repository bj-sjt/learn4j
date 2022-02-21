package com.itao.jline;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

public class JlineTest {

    public static void main(String[] args) {
        LineReader reader = LineReaderBuilder.builder().build();
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
