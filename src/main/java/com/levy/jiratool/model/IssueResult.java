package com.levy.jiratool.model;

import com.atlassian.jira.rest.client.domain.Comment;
import lombok.Data;

@Data
public class IssueResult {
    private String id;
    private Iterable<Comment> comments;
    private long spendTime;
    private boolean rejected;
    private String rejectedComment;
    private String rejectedReason;
}
