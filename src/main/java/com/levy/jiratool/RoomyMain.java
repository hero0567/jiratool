package com.levy.jiratool;

import com.levy.jiratool.rooomy.RooomyIssueService;

public class RoomyMain {
    public static void main(String[] args) {
        RooomyIssueService issueService = new RooomyIssueService();
        issueService.getRejectedIssueComments();
//        String jql = "project in (\"Amazon Factory WO4\", \"Amazon Factory WO5\") AND \"Responsible team\" is not EMPTY " +
//                "AND QA is not EMPTY AND status changed to (\"Unique Accepted by Customer\", \"Variation Accepted by Customer\") " +
//                "during (\"2020-2-14 00:00\", \"2020-2-15 00:00\")  AND resolution = Unresolved AND (Site = China OR Site is EMPTY)";

//        String jql = "project in (\"Amazon Customer WO4\", \"Amazon Customer WO5\") AND status changed to (\"Unique Remarks by Customer\", \"Variation Remarks by Customer\") during (startOfWeek(-1), endOfWeek(-1))  ";
//        issueService.getRejectedIssueCommentsByJql(jql);
//        System.exit(0);
    }
}
