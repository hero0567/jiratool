package com.levy.jiratool;

import com.levy.jiratool.rooomy.RooomyIssueService;

public class RoomyMain {
    public static void main(String[] args) {
        RooomyIssueService issueService = new RooomyIssueService();
        issueService.getRejectedIssueComments();
        System.exit(0);
    }
}
