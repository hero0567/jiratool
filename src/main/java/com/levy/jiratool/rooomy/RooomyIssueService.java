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
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class RooomyIssueService {

    private Map<String, String> rejectCause = new HashMap<>();
    private String formatter = "yyyy-MM-dd HH:mm:ss";
    private JiraClient jiraClient;
    private String[] removedAssignees = {"issue1", "issue2", "issue3", "amazon managem"};

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
            List<Comment> invertedOrderCommetns = new ArrayList<>();
            for (Comment comment : comments) {
                invertedOrderCommetns.add(0, comment);
            }
            Iterable<ChangelogGroup> changelog = jiraClient.getChangelog(issueKey.getId());
            issueResult.setId(issueKey.getId());
            issueResult.setName(issueKey.getName());
            issueResult.setComments(invertedOrderCommetns);
            issueResult.setChangelogs(changelog);
            long end = System.currentTimeMillis();
            issueResult.setSpendTime((end - begin) / 1000);
            log.info("Complete get issue comments of {}", issueKey.getId());
        } catch (Exception e) {
            log.error("Failed to get issue information.", e);
        }
        return issueResult;
    }

    private void mergeAssignee(IssueResult issueResult) {
        List<String> assignees = new ArrayList<>();
        issueResult.setAssignees(assignees);
        for (ChangelogGroup changelog : issueResult.getChangelogs()) {
            changelog.getItems().forEach(item -> {
                if ("assignee".equals(item.getField())) {
                    if (!Arrays.asList(removedAssignees).contains(item.getTo().toLowerCase())) {
                        assignees.add(item.getTo() + "(" + changelog.getCreated().toString(DateTimeFormat.forPattern(formatter)) + ")");
                    }
                }
            });
        }
    }

    private void mergeLastComments(IssueResult issueResult) {
        for (Comment comment : issueResult.getComments()) {
            for (String assignee : issueResult.getAssignees()) {
                if (assignee.toLowerCase().startsWith(comment.getAuthor().getName().toLowerCase() + "(")) {
                    if (issueResult.getLastComment() == null) {
                        issueResult.setLastComment(comment);
                    } else {
                        issueResult.setSecondComment(comment);
                        return;
                    }
                }
            }
        }
    }

    private void mergeIssueRejectedComments(IssueResult issueResult) {
        List<String> rejectResults = new ArrayList<>();
        issueResult.setRejectResults(rejectResults);
        String matchKey = "";
        for (Comment comment : issueResult.getComments()) {
            for (Map.Entry<String, String> entry : rejectCause.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (Pattern.compile(v).matcher(comment.getBody()).find()) {
                    matchKey = k;
                    break;
                }
            }
            if (StringUtils.isNotEmpty(matchKey)) {
                break;
            }
        }
        for (Map.Entry<String, String> entry : rejectCause.entrySet()) {
            if (matchKey.equals(entry.getKey())) {
                rejectResults.add("1");
            } else {
                rejectResults.add("");
            }
        }
    }

    private void convertIssue(List<IssueResult> issueResults) {
        for (IssueResult issueResult : issueResults) {
            mergeIssueRejectedComments(issueResult);
            mergeAssignee(issueResult);
            mergeLastComments(issueResult);
        }
    }

    public void getRejectedIssueComments() {
        String keyDefaultPath = LoadIssueKey.getIssueKeyDefaultPath();
        getRejectedIssueComments(keyDefaultPath);
    }

    public void getRejectedIssueComments(String keyPath) {
        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey(keyPath);
        List<IssueResult> issueResults = loadIssueCommentsAsync(issueKeys);
        convertIssue(issueResults);
        RooomyContextService contextService = new RooomyContextService();
        List<String> contents = contextService.getContent(issueResults, rejectCause);
        FileWriter fileWriter = new TextFileWriter();
        fileWriter.write(contents);
    }
}
