package com.levy.jiratool.rooomy;

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
            String content = String.join(";",
                    issueResult.getId(),
                    assigne,
                    String.valueOf(issueResult.getAssignees().size()),
                    rejectResults,
                    String.valueOf(issueResult.getSpendTime()));
            contentList.add(content);
        }
        return contentList;
    }

    public String getContentHeader(Map<String, String> rejectCause){
        List<String> header = new ArrayList<>();
        header.add("Issuekey(CUS)");
        header.add("Assignee");
        header.add("ACount");
        header.add(String.join(";", rejectCause.keySet()));
        header.add("Time");
        return String.join(";",header);
    }
}
