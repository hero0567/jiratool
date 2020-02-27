package com.levy.jiratool.lib;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Slf4j
public class JiraClient {

    private JiraRestClient jc;

    public JiraClient(JiraRestClient jc) {
        this.jc = jc;
    }

    public SearchResult searchJql(String jql) throws ExecutionException, InterruptedException {
        SearchResult r = jc.getSearchClient().searchJql(jql).get();
        return r;
    }

    public Iterable<Comment> getComments(String issueId) throws ExecutionException, InterruptedException {
        return jc.getIssueClient().getIssue(issueId).get().getComments();
    }

    public Iterable<Worklog> getWorklog(String issueId) throws ExecutionException, InterruptedException {
        IssueRestClient.Expandos[] expandArr = new IssueRestClient.Expandos[] { IssueRestClient.Expandos.CHANGELOG };
        return jc.getIssueClient().getIssue(issueId, Arrays.asList(expandArr)).get().getWorklogs();
    }

    public Iterable<ChangelogGroup> getChangelog(String issueId) throws ExecutionException, InterruptedException {
        IssueRestClient.Expandos[] expandArr = new IssueRestClient.Expandos[] { IssueRestClient.Expandos.CHANGELOG };
        return jc.getIssueClient().getIssue(issueId, Arrays.asList(expandArr)).get().getChangelog();
    }

    public Iterable<Attachment> getAttachments(String issueId) throws ExecutionException, InterruptedException {
        return jc.getIssueClient().getIssue(issueId).get().getAttachments();
    }

    public Iterable<BasicProject> getAllProjects() throws ExecutionException, InterruptedException {
        final Iterable<BasicProject> allProjects = jc.getProjectClient()
                .getAllProjects().get();
        return allProjects;
    }
}
