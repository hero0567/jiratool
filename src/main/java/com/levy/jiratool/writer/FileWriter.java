package com.levy.jiratool.writer;

import com.levy.jiratool.model.IssueResult;

import java.util.List;

public interface FileWriter {

    public void write(List<String> contents);
}
