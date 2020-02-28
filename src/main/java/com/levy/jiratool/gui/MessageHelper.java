package com.levy.jiratool.gui;

import lombok.Data;

import javax.swing.*;

@Data
public class MessageHelper {

    private JTextArea logTextArea;
    private long startTime = System.currentTimeMillis();

    private static MessageHelper log = new MessageHelper();

    private MessageHelper() {
    }

    public static MessageHelper getLog() {
        return log;
    }

    public void info(String msg) {
        if (logTextArea != null) {
            logTextArea.append(msg);
            logTextArea.append("\r\n");
        }
    }

    public void infot(String msg) {
        if (logTextArea != null) {
            logTextArea.append(msg);
            logTextArea.append(" it spend ");
            logTextArea.append((System.currentTimeMillis() - startTime) / 1000 + " seconds!");
            logTextArea.append("\r\n");
        }
    }
}
