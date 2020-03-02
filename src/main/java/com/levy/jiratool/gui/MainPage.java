package com.levy.jiratool.gui;

import com.levy.jiratool.rooomy.RooomyIssueCounter;
import com.levy.jiratool.rooomy.RooomyIssueService;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage extends JPanel {

    private static final long serialVersionUID = 1L;
    private JButton startBut, fileChooseBut, causeAddBtn;
    private JTextField dragPathField, choosePathField, causeFiled, jqlFiled;
    private JTextArea logTextArea;
    private MessageHelper messager = MessageHelper.getLog();

    public MainPage() {
        this.setLayout(null);
        buildLayout();
        completeLayout();
    }

    private void buildLayout() {
        JLabel tips = new JLabel("Please drag or choose jira issue key file.");
        tips.setBounds(170, 30, 400, 40);

        //drop file
        JLabel dragPath = new JLabel("Drag File :");
        dragPath.setBounds(30, 80, 100, 40);

        dragPathField = new JTextField();
        dragPathField.setBounds(100, 80, 250, 40);
        dragPathField.setEditable(false);

        startBut = new JButton("Start");
        startBut.setBounds(370, 80, 200, 40);

        //choose file
        JLabel choosePath = new JLabel("Choose File :");
        choosePath.setBounds(20, 150, 100, 40);

        choosePathField = new JTextField();
        choosePathField.setBounds(100, 150, 250, 40);
        choosePathField.setEditable(true);

        fileChooseBut = new JButton("Choose...");
        fileChooseBut.setBounds(370, 150, 200, 40);


        //jql filded
        JLabel jqlLabel = new JLabel("JIRA Search :");
        jqlLabel.setBounds(18, 210, 100, 40);

        jqlFiled = new JTextField();
        jqlFiled.setBounds(100, 210, 250, 40);
        jqlFiled.setEditable(true);

        //cause filed
        JLabel causeLabel = new JLabel("New Cause :");
        causeLabel.setBounds(22, 280, 100, 40);

        causeFiled = new JTextField();
        causeFiled.setBounds(100, 280, 250, 40);
        causeFiled.setEditable(true);

        causeAddBtn = new JButton("Add");
        causeAddBtn.setBounds(370, 280, 200, 40);

        //log window
        logTextArea = new JTextArea();
        messager.setLogTextArea(logTextArea);
        JScrollPane jsp = new JScrollPane(logTextArea);
        jsp.setBounds(50, 350, 520, 260);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        this.add(jsp);
        this.add(tips);
        this.add(dragPath);
        this.add(choosePath);
        this.add(dragPathField);
        this.add(choosePathField);
        this.add(startBut);
        this.add(fileChooseBut);
        this.add(jqlLabel);
        this.add(jqlFiled);
        this.add(causeLabel);
        this.add(causeFiled);
        this.add(causeAddBtn);
    }

    private void completeLayout() {
        // 实现拖拽功能, 获取JPG文件
        new DropTarget(dragPathField, DnDConstants.ACTION_COPY_OR_MOVE, new FileDropListener(dragPathField));
        fileChooseBut.addActionListener(new FileChooseListener(choosePathField));
        RooomyIssueService issueService = new RooomyIssueService();
        startBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String dragPath = dragPathField.getText().trim();
                String choosePath = choosePathField.getText().trim();
                String jql = jqlFiled.getText().trim();
                if (StringUtils.isEmpty(choosePath) && StringUtils.isEmpty(dragPath) && StringUtils.isEmpty(jql)) {
                    JOptionPane.showMessageDialog(null, "Please choose file or add search!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    JLabel running_label = new JLabel("Running ...    Please wait ...");
                    running_label.setBounds(130, 150, 300, 40);
                    add(running_label);
                    startBut.setEnabled(false);
                    repaint();
                    new Thread(() -> {
                        long startTime = System.currentTimeMillis();
                        try {
                            RooomyIssueCounter counter = RooomyIssueCounter.getInstance();
                            counter.setStart(startTime);
                            if(StringUtils.isEmpty(jql)){
                                String filePath = StringUtils.isNotEmpty(dragPath) ? dragPath : choosePath;
                                issueService.getRejectedIssueComments(filePath);
                            }else{
                                issueService.getRejectedIssueCommentsByJql(jql);
                            }
                            long endTime = System.currentTimeMillis();
                            messager.info("The operation is done in " + (endTime - startTime) / 1000 + " second!");
                            JOptionPane.showMessageDialog(MainPage.this, "The operation is done!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e) {
                            System.out.println(e);
                            long endTime = System.currentTimeMillis();
                            messager.info("The operation is abort " + (endTime - startTime) / 1000 + " second!");
                            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        remove(running_label);
                        startBut.setEnabled(true);
                        repaint();
                    }).start();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        causeAddBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cause = causeFiled.getText().trim();
                if (StringUtils.isEmpty(cause)) {
                    JOptionPane.showMessageDialog(null, "Cause should not empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                issueService.addCause(cause, cause);
                messager.info("Add new cause:" + cause);
            }
        });

    }


}
