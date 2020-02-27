package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.Comment;

import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.lib.JiraClientFactory;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.model.IssueResult;
import com.levy.jiratool.writer.FileWriter;
import com.levy.jiratool.writer.TextFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class RooomyIssueService {

    private Map<String, String> rejectCause = new HashMap<>();
    private String formatter = "yyyy-MM-dd HH:mm:ss";
    private JiraClient jiraClient;

    public RooomyIssueService() {
        rejectCause.put("Model-Detail", "Model.{0,3}Detail");
        rejectCause.put("Model-Dimensions", "di.{0,3}mension");
        rejectCause.put("Model-Geometry", "Model.{0,3}Geometry");
        rejectCause.put("Model-Error", "Model.{0,3}Error");
        rejectCause.put("Texture-Color", "Texture.{0,3}[Cc]olo(u)?r");
        rejectCause.put("Texture-Appearance", "Texture.{0,3}App.{0,3}earance");
        rejectCause.put("Process-Zip", "up.{0,3}load");
        rejectCause.put("Process-Missing Comment", "Process.{0,3}Missing Comment");

        log.info("Login...");
        jiraClient = JiraClientFactory.createJiraClient(
                "cn.fei.xiao",
                "fei.xiaoJIRApassword",
                "https://amazon.rooomy.com.cn");
    }

    public List<IssueResult> loadIssueCommentsAsync(List<IssueKey> issueKeys) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "200");
        List<IssueResult> issueResults = issueKeys.stream().parallel().map(issueKey -> {
            return loadIssueResult(jiraClient, issueKey);
        }).collect(Collectors.toList());
        return issueResults;
    }

    private IssueResult loadIssueResult(JiraClient jiraClient, IssueKey issueKey) {
        log.info("Try to get issue comments of {}", issueKey.getId());
        long begin = System.currentTimeMillis();
        IssueResult issueResult = new IssueResult();
        try {
            Iterable<Comment> comments = jiraClient.getComments(issueKey.getId());
            Iterable<ChangelogGroup> changelog = jiraClient.getChangelog(issueKey.getId());
            issueResult.setId(issueKey.getId());
            issueResult.setComments(comments);
            issueResult.setChangelogs(changelog);
            long end = System.currentTimeMillis();
            issueResult.setSpendTime((end - begin) / 1000);
            log.info("Complete get issue comments of {}", issueKey.getId());
        } catch (Exception e) {
            log.error("Failed to get issue information.", e);
        }
        return issueResult;
    }

    private void mergeAssignee(List<IssueResult> issueResults) {
        for (IssueResult issueResult : issueResults) {
            List<String> assignees = new ArrayList<>();
            issueResult.setAssignees(assignees);
            for (ChangelogGroup changelog : issueResult.getChangelogs()) {
                changelog.getItems().forEach(item -> {
                    if ("assignee".equals(item.getField())) {
                        assignees.add(item.getTo() + "(" + changelog.getCreated().toString(DateTimeFormat.forPattern(formatter)) + ")");
                    }
                });
            }
        }
    }

    private void mergeIssueRejectedComments(List<IssueResult> issueResults) {
        for (IssueResult issueResult : issueResults) {
            List<String> rejectResults = new ArrayList<>();
            issueResult.setRejectResults(rejectResults);
            rejectCause.forEach((k, v) -> {
                String rejectDate = "";
                for (Comment comment : issueResult.getComments()) {
                    if (Pattern.compile(v).matcher(comment.getBody()).find()) {
                        rejectDate = comment.getUpdateDate().toString(DateTimeFormat.forPattern(formatter));
                        log.debug("Issue({}) Found reject reason： {}, from: {}", issueResult.getId(), v, comment.getBody());
                    }
                }
                rejectResults.add(rejectDate);
            });
        }
    }

    public void getRejectedIssueComments() {
        String keyDefaultPath = LoadIssueKey.getIssueKeyDefaultPath();
        getRejectedIssueComments(keyDefaultPath);
    }

    public void getRejectedIssueComments(String keyPath) {
        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey(keyPath);
        List<IssueResult> issueResults = loadIssueCommentsAsync(issueKeys);
        mergeIssueRejectedComments(issueResults);
        mergeAssignee(issueResults);
        RooomyContextService contextService = new RooomyContextService();
        List<String> contents = contextService.getContent(issueResults, rejectCause);
        FileWriter fileWriter = new TextFileWriter();
        fileWriter.write(contents);
    }
}
