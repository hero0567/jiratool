package com.levy.jiratool.rooomy;

import com.levy.jiratool.model.IssueKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoadIssueKey {

    private static String issueKeyName = "issuekey.txt";

    public static List<IssueKey> loadIssueKey(){
        List<IssueKey> issueKeys = new ArrayList<>();
        try(InputStream inputStream = new ClassPathResource(issueKeyName).getInputStream()) {
            BufferedReader bufferedRreader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while (Strings.isNotBlank(line = bufferedRreader.readLine())){
                IssueKey issueKey = new IssueKey(line.trim());
                issueKeys.add(issueKey);
            }
        } catch (IOException e) {
            log.error("Failed to load issue key.", e);
        }
        return issueKeys;
    }
}
