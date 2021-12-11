package com.persybot.logger.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class PersyBotLogger {
    public static final Logger BOT_LOGGER;

    static {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationFactory.newConfigurationBuilder();

        RootLoggerComponentBuilder logger = builder.newRootLogger(Level.ALL);

        AppenderComponentBuilder consoleAppender = builder.newAppender("stdout", ConsoleAppender.PLUGIN_NAME)
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        AppenderComponentBuilder fileErrorAppender = builder.newAppender("fileError", RollingFileAppender.PLUGIN_NAME);

        fileErrorAppender.addAttribute("filePattern", "logs/%d{yyyy-MM-dd}.log");

        ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
        fileErrorAppender.addComponent(triggeringPolicy);


        LayoutComponentBuilder simpleLayout = builder.newLayout("PatternLayout");
        simpleLayout.addAttribute("pattern", "%highlight{%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level - %msg%n}").addAttribute("disableAnsi", false);

        LayoutComponentBuilder errorLayout = builder.newLayout("PatternLayout");
        errorLayout.addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level - %msg{nolookups}%xThrowable{separator(\n)}%n");

        consoleAppender.add(simpleLayout);
        fileErrorAppender.add(errorLayout);

        builder.add(consoleAppender);
        builder.add(fileErrorAppender);

        logger.add(builder.newAppenderRef("stdout").addAttribute("level", Level.ALL));
        logger.add(builder.newAppenderRef("fileError").addAttribute("level", Level.ERROR));

        builder.add(logger);

        builder.build();
        Configurator.initialize(builder.build());

        BOT_LOGGER = LogManager.getRootLogger();
    }

}
