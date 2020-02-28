package com.levy.jiratool.rooomy;

import lombok.Data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class RooomyIssueCounter {
    private final static RooomyIssueCounter rooomyIssueCounter = new RooomyIssueCounter();
    private long start;
    private long end;
    private int totalIssue;
    private AtomicInteger completedCounter = new AtomicInteger();

    private RooomyIssueCounter() {

    }

    public static RooomyIssueCounter getInstance() {
        return rooomyIssueCounter;
    }

}
