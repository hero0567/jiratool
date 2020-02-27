package com.levy.jiratool.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooseListener implements ActionListener {

    private JTextField choosePathField;

    public FileChooseListener(JTextField choosePathField) {
        this.choosePathField = choosePathField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
        jfc.showDialog(new JLabel(), "选择");
        File file=jfc.getSelectedFile();
        if (file != null){
            choosePathField.setText(file.getAbsolutePath());
        }
    }
}
