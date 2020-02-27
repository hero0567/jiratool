package com.levy.jiratool.model;

import com.atlassian.jira.rest.client.domain.Comment;
import lombok.Data;

import java.util.Map;

@Data
public class IssueResult {
    private String id;
    private Iterable<Comment> comments;
    private long spendTime;
    private Map<String, Boolean> rejectResults;
}
