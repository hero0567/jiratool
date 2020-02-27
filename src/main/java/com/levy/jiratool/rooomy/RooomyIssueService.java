package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Worklog;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.lib.JiraClientFactory;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.model.IssueResult;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RooomyIssueService {

    private Map<String, String> rejectCause = new HashMap<>();

    public RooomyIssueService(){
        rejectCause.put("Model-Detail" ,"Model.{0,3}Detail");
        rejectCause.put("Model-Dimensions" ,"di.{0,3}mension");
        rejectCause.put("Model-Geometry" ,"Model.{0,3}Geometry");
        rejectCause.put("Model-Error" ,"Model.{0,3}Error");
        rejectCause.put("Texture-Color" ,"[Cc]olo(u)?r");
        rejectCause.put("Texture-Appearance" ,"App.{0,3}earance");
        rejectCause.put("Process-Zip" ,"up.{0,3}load");
        rejectCause.put("Process-Missing Comment" ,"Process.{0,3}Missing Comment");
    }

    public List<IssueResult> loadIssueComments() {
        log.info("Login...");
        JiraClient jiraClient = JiraClientFactory.createJiraClient(
                "cn.fei.xiao",
                "fei.xiaoJIRApassword",
                "https://amazon.rooomy.com.cn");

        List<IssueResult> issueResults = new ArrayList<>();
        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey();
        log.info("Start query comments.");
        for (IssueKey issueKey : issueKeys) {
            IssueResult issueResult = loadIssueResult(jiraClient, issueKey);
            issueResults.add(issueResult);
        }
        return issueResults;
    }

    public List<IssueResult> loadIssueCommentsAsync() {
        log.info("Login...");
        JiraClient jiraClient = JiraClientFactory.createJiraClient(
                "cn.fei.xiao",
                "fei.xiaoJIRApassword",
                "https://amazon.rooomy.com.cn");

        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey();
        log.info("Start query comments.");
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
        Iterable<Comment> comments = jiraClient.getComments(issueKey.getId());
        Iterable<Worklog> worklogs = jiraClient.getWorklog(issueKey.getId());
        issueResult.setId(issueKey.getId());
        issueResult.setComments(comments);
        long end = System.currentTimeMillis();
        issueResult.setSpendTime((end - begin) / 1000);
        log.info("Complete get issue comments of {}", issueKey.getId());
        return issueResult;
    }

    private void checkIssueRejected(List<IssueResult> issueResults) {

        for (IssueResult issueResult : issueResults) {
            Map<String, Boolean> rejectResults = new HashMap<>();
            issueResult.setRejectResults(rejectResults);
            for (Comment comment : issueResult.getComments()) {
                rejectCause.forEach((k,v)->{
                    if (comment.getBody().matches(v)) {
                        rejectResults.put(k, true);
                    }else{
                        rejectResults.put(k, false);
                    }
                });
            }
        }
    }

    private void writeIssueResult(List<IssueResult> issueResults) {
        String fname = "./result.txt";
        try {
            FileOutputStream fs = new FileOutputStream(new File(fname));
            PrintStream p = new PrintStream(fs);
            for (IssueResult issueResult : issueResults) {
                p.println(String.join(";",
                        issueResult.getId(),
                        String.valueOf(issueResult.getSpendTime())));
            }
            p.flush();
            p.close();
            fs.close();
        } catch (Exception e) {
            log.error("Failed to save data.");
        }
        log.info("Save data success.");
    }

    public void getRejectedIssueComments(){
        List<IssueResult> issueResults = loadIssueCommentsAsync();
        checkIssueRejected(issueResults);
        writeIssueResult(issueResults);
    }


    public static void main(String[] args) {
        RooomyIssueService issueService = new RooomyIssueService();
        issueService.getRejectedIssueComments();
    }
}
