package com.levy.jiratool.gui;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class FileDropListener extends DropTargetAdapter {

    private JTextField dragPathField;

    public FileDropListener(JTextField dragPathField) {
        this.dragPathField = dragPathField;
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {  //如果拖入的文件格式受支持
                dtde.acceptDrop(dtde.getDropAction());  //接收拖拽来的数据
                @SuppressWarnings("unchecked")
                List<File> files = (List<File>) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                dtde.dropComplete(true); //指示拖拽操作已完成

                dragPathField.setText(files.get(0).getAbsolutePath());
            } else {
                dtde.rejectDrop();  //拒绝拖拽来的数据
                JOptionPane.showMessageDialog(null, "不支持拖入的文件格式！");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
