package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Attachment;
import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.ChangelogItem;
import com.atlassian.jira.rest.client.domain.Comment;

import com.levy.jiratool.gui.MessageHelper;
import com.levy.jiratool.lib.JiraClient;
import com.levy.jiratool.lib.JiraClientFactory;
import com.levy.jiratool.model.IssueKey;
import com.levy.jiratool.model.IssueResult;
import com.levy.jiratool.writer.ExcelFileWriter;
import com.levy.jiratool.writer.FileWriter;
import com.levy.jiratool.writer.TextFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class RooomyIssueService {

    private Map<String, String> rejectCause = new HashMap<>();
    private String formatter = "yyyy-MM-dd HH:mm:ss";
    private JiraClient jiraClient;
    private String[] removedAssignees = {"issue1", "issue2", "issue3", "amazon managem"};
    private MessageHelper messager = MessageHelper.getLog();
    private RooomyIssueCounter counter = RooomyIssueCounter.getInstance();

    public RooomyIssueService() {
        rejectCause.put("Model-Detail", "Model.{0,3}Detail");
        rejectCause.put("Model-Dimensions", "[Dd]i.{0,3}mension");
        rejectCause.put("Model-Geometry", "Model.{0,3}Geometry");
        rejectCause.put("Model-Error", "Model.{0,3}Error");
        rejectCause.put("Texture-Color", "[Cc]olo(u)?r");
        rejectCause.put("Texture-Appearance", "App.{0,3}earance");
        rejectCause.put("Process-Zip", "up.{0,3}load");
        rejectCause.put("Process-Missing Comment", "Process.{0,3}Missing Comment");

        log.info("Login...");
        jiraClient = JiraClientFactory.createJiraClient(
                "cn.fei.xiao",
                "fei.xiaoJIRApassword",
                "https://amazon.rooomy.com.cn");
    }

    public List<IssueResult> loadIssueCommentsAsync(List<IssueKey> issueKeys) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "32");
        List<IssueResult> issueResults = issueKeys.stream().parallel().map(issueKey -> {
            return loadIssueResult(jiraClient, issueKey);
        }).collect(Collectors.toList());
        return issueResults;
    }

    private IssueResult loadIssueResult(JiraClient jiraClient, IssueKey issueKey) {
        log.info("Try to get issue comments of {}", issueKey.getId());
        long begin = System.currentTimeMillis();
        IssueResult issueResult = new IssueResult();
        issueResult.setId(issueKey.getId());
        issueResult.setName(issueKey.getName());
        try {
            int retry = 0;
            while (!loadIssueResult(jiraClient, issueKey, issueResult)) {
                retry++;
                if (retry == 3) {
                    log.warn("Issue {} already retry 3 times. will skip it.", issueKey.getId());
                    break;
                }
                log.info("Issue {} will retry {} time.", issueKey.getId(), retry);
            }
            log.info("Complete get issue {}", issueKey.getId());
        } catch (Exception e) {
            log.error("Failed to get issue(" + issueKey.getId() + ") information.", e);
        }
        int completed = counter.getCompletedCounter().incrementAndGet();
        if (completed % 100 == 0) {
            messager.infot("Completed load " + completed + " issues.");
        }
        long end = System.currentTimeMillis();
        issueResult.setSpendTime((end - begin) / 1000);
        return issueResult;
    }

    private boolean loadIssueResult(JiraClient jiraClient, IssueKey issueKey, IssueResult issueResult) {
        try {
            Iterable<Comment> comments = jiraClient.getComments(issueKey.getId());
            Iterable<ChangelogGroup> changelog = jiraClient.getChangelog(issueKey.getId());
            Iterable<Attachment> attachments = jiraClient.getAttachments(issueKey.getId());
            issueResult.setComments(comments);
            issueResult.setChangelogs(changelog);
            issueResult.setAttachments(attachments);
            log.info("Complete get issue {}", issueKey.getId());
        } catch (RestClientException | ExecutionException | InterruptedException e) {
            return false;
        }
        return true;
    }

    private void mergeAssignee(IssueResult issueResult) {
        try {
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
        } catch (Exception e) {
            log.error("Failed to merge assignee for {}", issueResult.getId());
        }

    }

    private void mergeLastComments(IssueResult issueResult) {
        try {
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
        } catch (Exception e) {
            log.error("Failed to merge last comments for {}", issueResult.getId());
        }

    }

    private void mergeIssueRejectedComments(IssueResult issueResult) {
        try {
            List<String> rejectResults = new ArrayList<>();
            issueResult.setRejectResults(rejectResults);
            List<String> matchedKeys = new ArrayList<>();
            for (Comment comment : issueResult.getComments()) {
                for (Map.Entry<String, String> entry : rejectCause.entrySet()) {
                    String k = entry.getKey();
                    String v = entry.getValue();
                    if (Pattern.compile(v).matcher(comment.getBody()).find()) {
                        matchedKeys.add(k);
                    }
                }
                //only compare first comment
                break;
            }
            for (Map.Entry<String, String> entry : rejectCause.entrySet()) {
                if (matchedKeys.contains(entry.getKey())) {
                    rejectResults.add("1");
                } else {
                    rejectResults.add("");
                }
            }
        } catch (Exception e) {
            log.error("Failed to merge comments for {}", issueResult.getId());
        }

    }

    /**
     * ChangelogGroup{author=BasicUser{name=natrajar, displayName=R,Natarajan, self=https://amazon.rooomy.com.cn/rest/api/2/user?username=natrajar},
     * created=2020-01-18T01:11:23.394+08:00, items=[ChangelogItem{fieldType=CUSTOM, field=External QA Round, from=null, fromString=3, to=null, toString=4},
     * ChangelogItem{fieldType=JIRA, field=status, from=10818, fromString=Unique Ready for Customer, to=10819, toString=Unique Remarks by Customer}]}
     * <p>
     * Variation Remarks by Customer [ 10826 ]
     * Unique Remarks by Customer [ 10819 ]
     *
     * @param issueResult
     */
    private void mergeRemark(IssueResult issueResult) {
        try {
            ChangelogGroup lastRemark = null;
            out:
            for (ChangelogGroup changelog : issueResult.getChangelogs()) {
                for (ChangelogItem item : changelog.getItems()) {
                    if ("status".equals(item.getField())) {
                        if ("10819".equals(item.getTo()) || "10826".equals(item.getTo())) {
                            issueResult.setLastRemark(changelog);
                            lastRemark = changelog;
                            break out;
                        }
                    }
                }
            }
            if (lastRemark == null) {
                log.warn("{} not found remark.", issueResult.getId());
                return;
            }
            for (Comment comment : issueResult.getComments()) {
                if (comment.getAuthor().getName().toLowerCase().equals(lastRemark.getAuthor().getName().toLowerCase())
                        && timeAround(lastRemark.getCreated(), comment.getUpdateDate())) {
                    issueResult.setRemarkComment(true);
                    break;
                }

            }
            for (Attachment attachment : issueResult.getAttachments()) {
                if (attachment.getAuthor().getName().toLowerCase().equals(lastRemark.getAuthor().getName().toLowerCase())
                        && timeAround(lastRemark.getCreated(), attachment.getCreationDate())) {
                    issueResult.setRemarkAttachment(true);
                    break;
                }

            }
        } catch (Exception e) {
            log.error("Failed to merge remark for {}", issueResult.getId());
        }

    }

    private boolean timeAround(DateTime givenTime, DateTime checkTime) {
        long around = 3 * 60 * 60 * 1000;
        long givenMillis = givenTime.getMillis();
        long checkMillis = checkTime.getMillis();
        if (Math.abs(checkMillis - givenMillis) < around) {
            log.info("Time Around check: {}, {}", givenTime, checkTime);
            return true;
        }
        return false;
    }

    private void convertIssue(List<IssueResult> issueResults) {
        for (IssueResult issueResult : issueResults) {
            mergeIssueRejectedComments(issueResult);
            mergeAssignee(issueResult);
            mergeLastComments(issueResult);
            mergeRemark(issueResult);
        }
    }

    public void getRejectedIssueComments() {
        String keyDefaultPath = LoadIssueKey.getIssueKeyDefaultPath();
        getRejectedIssueComments(keyDefaultPath);
    }

    public void getRejectedIssueComments(String keyPath) {
        messager.setStartTime(System.currentTimeMillis());
        List<IssueKey> issueKeys = LoadIssueKey.loadIssueKey(keyPath);
        messager.infot("Totally load " + issueKeys.size() + " valid issue keys.");

        List<IssueResult> issueResults = loadIssueCommentsAsync(issueKeys);
        messager.infot("Completed load all(" + issueKeys.size() + ") issues.");

        convertIssue(issueResults);
        RooomyContextService contextService = new RooomyContextService();
        List<String> contents = contextService.getContent(issueResults, rejectCause);
        FileWriter fileWriter = new ExcelFileWriter();
        fileWriter.write(contents);
    }
}
