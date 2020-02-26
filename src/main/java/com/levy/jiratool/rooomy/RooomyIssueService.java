package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.lib.JiraClientFactory;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.model.IssueResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class RooomyIssueService {

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
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism","200");
        List<IssueResult> issueResults = issueKeys.stream().parallel().map(issueKey ->{
            return loadIssueResult(jiraClient, issueKey);
        }).collect(Collectors.toList());
        return issueResults;
    }

    private IssueResult loadIssueResult(JiraClient jiraClient, IssueKey issueKey) {
        log.info("Try to get issue comments of {}", issueKey.getId());
        long begin = System.currentTimeMillis();
        IssueResult issueResult = new IssueResult();
        Iterable<Comment> comments = jiraClient.getComments(issueKey.getId());
        issueResult.setId(issueKey.getId());
        issueResult.setComments(comments);
        long end = System.currentTimeMillis();
        issueResult.setSpendTime((end - begin) / 1000);
        return issueResult;
    }


    public static void main(String[] args) {
        RooomyIssueService issueService = new RooomyIssueService();
        List<IssueResult> issueResults = issueService.loadIssueCommentsAsync();
        for(IssueResult issueResult: issueResults){
            for(Comment comment: issueResult.getComments()){
                log.info("comment: {} {} {}", comment.getAuthor(),comment.getUpdateDate(), comment.getBody());
            }
        }
    }
}
