package com.itao.jline;

import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.widget.TailTipWidgets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TailTipTest {

    public static void main(String[] args) throws IOException {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(TerminalBuilder.terminal())
                //.completer(completer)
                //.parser(parser)
                .build();
        Map<String, CmdDesc> tailTips = new HashMap<>();
        Map<String, List<AttributedString>> widgetOpts = new HashMap<>();
        List<AttributedString> mainDesc = Arrays.asList(new AttributedString("widget -N new-widget [function-name]")
                , new AttributedString("widget -D widget ...")
                , new AttributedString("widget -A old-widget new-widget")
                , new AttributedString("widget -U string ...")
                , new AttributedString("widget -l [options]")
        );
        widgetOpts.put("-N", Arrays.asList(new AttributedString("Create new widget")));
        widgetOpts.put("-D", Arrays.asList(new AttributedString("Delete widgets")));
        widgetOpts.put("-A", Arrays.asList(new AttributedString("Create alias to widget")));
        widgetOpts.put("-U", Arrays.asList(new AttributedString("Push characters to the stack")));
        widgetOpts.put("-l", Arrays.asList(new AttributedString("List user-defined widgets")));

        tailTips.put("widget", new CmdDesc(mainDesc, ArgDesc.doArgNames(Arrays.asList("[pN...]")), widgetOpts));

        // Create tailtip widgets that uses description window size 5 and
        // does not display suggestions after the cursor
        TailTipWidgets tailtipWidgets = new TailTipWidgets(reader, tailTips, 5, TailTipWidgets.TipType.COMPLETER);
        // Enable autosuggestions
        tailtipWidgets.enable();
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
