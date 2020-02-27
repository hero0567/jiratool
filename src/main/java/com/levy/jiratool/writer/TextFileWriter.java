package com.levy.jiratool.writer;

import com.levy.jiratool.model.IssueResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

@Slf4j
public class TextFileWriter implements FileWriter {

    @Override
    public void write(List<String> contents) {
        String fname = "./issueresult.txt";
        try (FileOutputStream fs = new FileOutputStream(new File(fname));
             PrintStream p = new PrintStream(fs);
        ) {
            for (String content : contents){
                p.println(content);
            }
        } catch (Exception e) {
            log.error("Failed to save data.");
        }
        log.info("Save data success.");
    }
}
