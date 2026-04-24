package com.quiz.model;

import java.util.ArrayList;
import java.util.List;

public class StartQuizResponse {
    private String regNo;
    private String setId;
    private List<PollSummary> polls = new ArrayList<>();
    private List<LeaderboardEntry> leaderboard = new ArrayList<>();
    private SubmitResponse submission;
    private String error;

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public List<PollSummary> getPolls() {
        return polls;
    }

    public void setPolls(List<PollSummary> polls) {
        this.polls = polls;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(List<LeaderboardEntry> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public SubmitResponse getSubmission() {
        return submission;
    }

    public void setSubmission(SubmitResponse submission) {
        this.submission = submission;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
