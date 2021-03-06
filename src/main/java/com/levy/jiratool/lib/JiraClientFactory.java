package com.levy.jiratool.lib;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class JiraClientFactory {

    //    private String SERVER_NAME = "https://nedsenseloft.atlassian.net";
//    private String USERNAME = "cn.manyun.liu";
//    private String PASSWORD = "lmy4563673";
    private String username;
    private String password;
    private JiraRestClient jc;

    public static JiraClient createJiraClient(String username, String password, String host) {
        try {
            AsynchronousJiraRestClientFactory f = new AsynchronousJiraRestClientFactory();
            JiraRestClient jc = f.createWithBasicHttpAuthentication(new URI(host),
                    username, password);
            return new JiraClient(jc);
        } catch (URISyntaxException e) {
            log.error("Failed create jira client.", e);
        }
        return null;
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        JiraClient instance = JiraClientFactory.createJiraClient("cn.fei.xiao", "fei.xiaoJIRApassword", "https://amazon.rooomy.com.cn");
        Iterable<BasicProject> project = instance.getAllProjects();
        System.out.println(project);
    }
}
