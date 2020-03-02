package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.levy.jiratool.gui.MessageHelper;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.model.IssueKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LoadIssueKey {

    private static String issueKeyName = "issuekey.txt";
    private static MessageHelper messager = MessageHelper.getLog();

    public static String getIssueKeyDefaultPath() {

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
                issueKey.setId(line);
                issueKeys.add(issueKey);
            }
        } catch (IOException e) {
            log.error("Failed to load issue key.", e);
        }
        log.info("Load {} issue keys.", issueKeys.size());
        return issueKeys;
    }


    public static List<IssueKey> loadIssueKeyByJql(JiraClient jiraClient, String jql) {
        List<IssueKey> issueKeys = new ArrayList<>();
        try {
            int start = 0;
            while (true) {
                int max = 1000;
                int end = start + max;
                SearchResult searchResult = loadSearchResult(jiraClient, jql, max, start);
                for (BasicIssue issue : searchResult.getIssues()) {
                    IssueKey issueKey = new IssueKey();
                    issueKey.setId(issue.getKey());
                    issueKeys.add(issueKey);
                }
                if (issueKeys.size() < end) {
                    log.info("Result less than 1000, load it one time.");
                    break;
                }
                messager.infot("Load " + issueKeys.size() + " issue keys.");
                log.info("Load {} issue, totally {} issue.", searchResult.getMaxResults(), searchResult.getTotal());
                start = end;
            }

        } catch (Exception e) {
            messager.infot("Crash when search jira.");
            log.error("Failed to search from jira:", e);
        }
        return issueKeys;
    }

    private static SearchResult loadSearchResult(JiraClient jiraClient, String jql, int max, int start) throws ExecutionException, InterruptedException {
        SearchResult searchResult = jiraClient.searchJql(jql, max, start);
        return searchResult;
    }

}
