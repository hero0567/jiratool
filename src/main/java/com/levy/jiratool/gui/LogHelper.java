package com.levy.jiratool.gui;

import javax.swing.*;

public class LogHelper {

    private JTextArea logTextArea;

    private static LogHelper log = new LogHelper();

    private LogHelper(){}

    public static LogHelper getLog() {
        return log;
    }

    public void info(String msg){
        logTextArea.append(msg);
        logTextArea.append("\r\n");
    }

    public void setLogTextArea(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
    }
}
