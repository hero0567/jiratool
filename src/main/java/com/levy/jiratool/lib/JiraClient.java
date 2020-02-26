package com.levy.jiratool.lib;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

@Slf4j
public class JiraClient {

    private JiraRestClient jc;

    public JiraClient(JiraRestClient jc) {
        this.jc = jc;
    }

    public SearchResult searchJql(String jql) {
        SearchResult r = jc.getSearchClient().searchJql(jql, null);
        return r;
    }

    public Iterable<Comment> getComments(String issueId){
        NullProgressMonitor pm = new NullProgressMonitor();
        return jc.getIssueClient().getIssue(issueId, pm).getComments();
    }

    public Iterable<BasicProject> getAllProjects() {
        NullProgressMonitor pm = new NullProgressMonitor();
        final Iterable<BasicProject> allProjects = jc.getProjectClient()
                .getAllProjects(pm);
        return allProjects;
    }
}
