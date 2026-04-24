package com.quiz.model;

public class LeaderboardEntry {
    private String participant;
    private Integer totalScore;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String participant, Integer totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
}
