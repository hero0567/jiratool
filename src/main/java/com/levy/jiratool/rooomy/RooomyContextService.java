package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.levy.jiratool.model.IssueResult;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RooomyContextService {

    private String formatter = "yyyy-MM-dd HH:mm:ss";

    public List<String> getContent(List<IssueResult> issueResults, Map<String, String> rejectCause) {
        List<String> contentList = new ArrayList<>();
        String header = getContentHeader(rejectCause);
        contentList.add(header);
        for (IssueResult issueResult : issueResults) {
            //String assigne = String.join("->", issueResult.getAssignees());
            String commentAuthors = String.join("->", issueResult.getCommentAuthors());
            String rejectResults = String.join(";", issueResult.getRejectResults());
            String secondComment = "";
            if (issueResult.getSecondComment() != null) {
                Comment comment = issueResult.getSecondComment();
                secondComment = comment.getAuthor().getDisplayName() + ":" + comment.getBody()
                        .replaceAll("\r\n", "")
                        .replaceAll(";", "");
            }
            String lastComment = "";
            String lastCommentUpdateDate = "";
            if (issueResult.getLastComment() != null) {
                Comment comment = issueResult.getLastComment();
                lastComment = comment.getAuthor().getDisplayName() + ":" + comment.getBody()
                        .replaceAll("\r\n", "")
                        .replaceAll(";", "");

                lastCommentUpdateDate = issueResult.getLastComment().getUpdateDate().toString(DateTimeFormat.forPattern(formatter));
            }

            String remarkComment = issueResult.isRemarkComment() ? "Yes" : "No";
            String remarkAttachment = issueResult.isRemarkAttachment() ? "Yes" : "No";
            String content = String.join(";",
                    issueResult.getId(),
                    commentAuthors,
                    String.valueOf(issueResult.getCommentAuthors().size()),  //ACount
                    issueResult.getStatus(),
                    issueResult.getTicketType(),
                    issueResult.getUniqueValue(),
                    issueResult.getVariationValue(),        //Variation Qty
                    issueResult.getAssignee(),
                    issueResult.getInternalQa(),
                    issueResult.getInternalQaRoundValue(),
                    issueResult.getExternalQaRoundValue(),
                    rejectResults,                          //reject
                    lastCommentUpdateDate,
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
        header.add("Issuekey(CUS)");
        header.add("Comment Authors");
        header.add("Comment Author Count");
        header.add("Status");
        header.add("Ticket Type");
        header.add("Unique Qty");
        header.add("Variation Qty");
        header.add("External QA");
        header.add("Internal QA");
        header.add("Internal QA Round");
        header.add("External QA Round");
        header.add(String.join(";", rejectCause.keySet()));   //reject
        header.add("Remark Last Date");
        header.add("Remark Comment");
        header.add("Remark Attachment");
        header.add("Second Comment");
        header.add("Last Comment");
        return String.join(";", header);
    }
}
