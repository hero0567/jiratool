package com.levy.jiratool.model;

import com.atlassian.jira.rest.client.domain.Attachment;
import com.atlassian.jira.rest.client.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.domain.Comment;
import lombok.Data;

import java.util.List;

@Data
public class IssueResult {
    private String id;
    private String name;
    private long spendTime;
    private Iterable<Comment> comments;
    private Iterable<ChangelogGroup> changelogs;
    private Iterable<Attachment> attachments;
    private List<String> assignees;
    private List<String> rejectResults;
    private Comment lastComment;
    private Comment secondComment;
    private ChangelogGroup lastRemark;
    private boolean remarkAttachment;
    private boolean remarkComment;
}
