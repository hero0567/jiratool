package com.levy.jiratool.rooomy;

import com.levy.jiratool.model.IssueKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoadIssueKey {

    private static String issueKeyName = "issuekey.txt";

    public static String getIssueKeyDefaultPath(){

        try {
            return new ClassPathResource(issueKeyName).getFile().getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to load default issuekey.txt.");
        }
        return "";
    }

    public static List<IssueKey> loadIssueKey(String keyPath) {
        List<IssueKey> issueKeys = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(keyPath);
             BufferedReader bufferedRreader = new BufferedReader(new InputStreamReader(inputStream));) {
            String line;
            while (StringUtils.isNotBlank(line = bufferedRreader.readLine())) {
                IssueKey issueKey = new IssueKey();
                String[] keyInfo = line.split(";", 2);
                issueKey.setId(keyInfo[1]);
                issueKey.setName(keyInfo[0]);
                issueKeys.add(issueKey);
            }
        } catch (IOException e) {
            log.error("Failed to load issue key.", e);
        }
        log.info("Load {} issue keys.", issueKeys.size());
        return issueKeys;
    }
}
