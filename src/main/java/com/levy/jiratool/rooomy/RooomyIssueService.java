package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.lib.JiraClientFactory;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.model.IssueResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RooomyIssueService {

    public List<IssueResult> loadIssueComments() {
        JiraClient jiraClient = JiraClientFactory.createJiraClient(
                "cn.fei.xiao",
                "fei.xiaoJIRApassword",
                "https://amazon.rooomy.com.cn");


        List<IssueResult> issueResults = new ArrayList<>();
        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey();
        for (IssueKey issueKey : issueKeys) {
            IssueResult issueResult = loadIssueResult(jiraClient, issueKey);
            issueResults.add(issueResult);
        }
        return issueResults;
    }

    private IssueResult loadIssueResult(JiraClient jiraClient, IssueKey issueKey) {
        log.info("Try to get issue comments of {}", issueKey.getId());
        IssueResult issueResult = new IssueResult();
        Iterable<Comment> comments = jiraClient.getComments(issueKey.getId());
        issueResult.setId(issueKey.getId());
        issueResult.setComments(comments);
        return issueResult;
    }

    public static void main(String[] args) {
        RooomyIssueService issueService = new RooomyIssueService();
        issueService.loadIssueComments();
    }
}
