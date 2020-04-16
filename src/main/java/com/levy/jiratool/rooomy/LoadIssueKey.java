package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.levy.jiratool.gui.MessageHelper;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.writer.ExcelFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class LoadIssueKey {

    private static String issueKeyName = "D:\\work\\jiratool\\src\\main\\resources\\RootCause_Back.xlsx";
    private static MessageHelper messager = MessageHelper.getLog();

    public static String getIssueKeyDefaultPath() {
        return issueKeyName;
    }

    public static List<IssueKey> loadIssueKey(String keyPath) {
        ExcelFileWriter excelFileWriter = new ExcelFileWriter();
        return excelFileWriter.read(keyPath);
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
