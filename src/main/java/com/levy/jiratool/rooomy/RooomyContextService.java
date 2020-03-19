package com.levy.jiratool.rooomy;

import com.atlassian.jira.rest.client.domain.Comment;
import com.google.common.base.Strings;
import com.levy.jiratool.model.IssueResult;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RooomyContextService {

    private String formatter = "yyyy-MM-dd HH:mm:ss";

    public List<String> getContent(List<IssueResult> issueResults, Map<String, String> rejectCause) {
        List<String> contentList = new ArrayList<>();
        String header = getContentHeader(rejectCause);
        contentList.add(header);
        for (IssueResult issueResult : issueResults) {
            try {
                //String assigne = String.join("->", issueResult.getAssignees());
                String commentAuthors = String.join("->", issueResult.getCommentAuthors());
                String rejectResults = String.join(";", issueResult.getRejectResults());

                //comment body
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

                int uniqueValue = getStringInt(issueResult.getUniqueValue());
                int variationValue = getStringInt(issueResult.getVariationValue());
                int maxQty = Math.max(uniqueValue, variationValue);

                //QA changed
                String qaChanged = "";
                if (issueResult.getSecondComment() != null && issueResult.getLastComment() != null) {
                    if (!issueResult.getSecondComment().getAuthor().getDisplayName().equals(issueResult.getLastComment().getAuthor().getDisplayName())) {
                        //not equal, qa changed
                        qaChanged = String.valueOf(maxQty);
                    }
                }

                String remarkComment = issueResult.isRemarkComment() ? "" : String.valueOf(maxQty);
                String remarkAttachment = issueResult.isRemarkAttachment() ? "" : String.valueOf(maxQty);
                String content = String.join(";",
                        issueResult.getId(),
                        commentAuthors,
                        String.valueOf(issueResult.getCommentUniqAuthors().size()),  //ACount
                        issueResult.getStatus(),
                        issueResult.getTicketType(),
                        issueResult.getUniqueValue(),
                        issueResult.getVariationValue(),        //Variation Qty
                        issueResult.getAssignee(),
                        issueResult.getInternalQa(),
                        issueResult.getInternalQaRoundValue(),
                        issueResult.getExternalQaRoundValue(),
                        issueResult.getLastReadyForCustomerDate(),
                        rejectResults,                          //reject
                        lastCommentUpdateDate,
                        remarkComment,
                        remarkAttachment,
                        qaChanged,
                        secondComment,
                        lastComment);
                contentList.add(content);
            } catch (Exception e) {
                log.error("Failed to combine content for" + issueResult.getId(), e);
            }
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
        header.add("Last Ready For Customer");
        header.add(String.join(";", rejectCause.keySet()));   //reject
        header.add("Remark Last Date");
        header.add("Remark Comment");
        header.add("Remark Attachment");
        header.add("QA Changed(Last Two Round)");
        header.add("Second Comment");
        header.add("Last Comment");
        return String.join(";", header);
    }

    private int getStringInt(String s) {
        int v = 0;
        if (Strings.isNullOrEmpty(s)) {
            return 0;
        }

        try {
            v = Integer.valueOf(s);
        } catch (Exception e) {
            log.error("Failed convert string to integer: {}", s);
        }
        return v;
    }
}
