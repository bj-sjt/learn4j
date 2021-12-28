package com.itao.springshell.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class JLineConfig {

    /**
     * 指定命令提示符 itao:>
     */
    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("itao:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
