package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.levy.jiratool.model.IssueResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RooomyContextService {

    public List<String> getContent(List<IssueResult> issueResults, Map<String, String> rejectCause) {
        List<String> contentList = new ArrayList<>();
        String header = getContentHeader(rejectCause);
        contentList.add(header);
        for (IssueResult issueResult : issueResults) {
            String assigne = String.join("->", issueResult.getAssignees());
            String rejectResults = String.join(";", issueResult.getRejectResults());
            String secondComment = "";
            if (issueResult.getSecondComment() != null) {
                Comment comment = issueResult.getSecondComment();
                secondComment = comment.getAuthor().getDisplayName() + ":" + comment.getBody().replaceAll("\r\n", "");
            }
            String lastComment = "";
            if (issueResult.getLastComment() != null) {
                Comment comment = issueResult.getLastComment();
                lastComment = comment.getAuthor().getDisplayName() + ":" + comment.getBody().replaceAll("\r\n", "");
            }
            String remarkComment = issueResult.isRemarkComment() ? "Yes" : "No";
            String remarkAttachment = issueResult.isRemarkAttachment() ? "Yes" : "No";
            String content = String.join(";",
                    issueResult.getName(),
                    issueResult.getId(),
                    assigne,
                    String.valueOf(issueResult.getAssignees().size()),
                    rejectResults,
                    remarkComment,
                    remarkAttachment,
                    secondComment,
                    lastComment);
            contentList.add(content);
        }
        return contentList;
    }

    public String getContentHeader(Map<String, String> rejectCause) {
        List<String> header = new ArrayList<>();
        header.add("QA");
        header.add("Issuekey(CUS)");
        header.add("Assignee");
        header.add("ACount");
        header.add(String.join(";", rejectCause.keySet()));
        header.add("Remark Comment");
        header.add("Remark Attachment");
        header.add("Second Comment");
        header.add("Last Comment");
        return String.join(";", header);
    }
}
