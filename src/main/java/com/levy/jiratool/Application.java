package com.levy.jiratool;

import com.levy.jiratool.gui.MainPage;

import javax.swing.*;
import java.util.Date;

public class Application extends JFrame {

    private static final long serialVersionUID = 1L;

    public Application() {
        super("JIRA Issue Tool");
        this.getContentPane().add(new MainPage());
        this.setSize(620, 700);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    public static void main(String[] args) {
        new Application();
    }
}
